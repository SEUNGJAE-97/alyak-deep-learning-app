import json
import logging
import os
import queue
from threading import Lock
from typing import Any, Generator
from uuid import uuid4

from ultralytics import YOLO

from app.schemas.training_schema import TrainRequest
from app.utils.callback import notify_spring_completion

logger = logging.getLogger(__name__)

# 학습된 모델 경로 및 데이터셋 yaml 경로
BASE_MODEL_PATH = os.getenv("BASE_MODEL_PATH", "models/pill_trained.pt")
DATASET_YAML_PATH = os.getenv("DATASET_YAML_PATH", "datasets/pill/data.yaml")

# freeze 파라미터 변환
FREEZE_MAP: dict[str, int | None] = {
    "none": None,   # 전체 레이어 학습
    "light": 3,     # 앞 3개 레이어 고정 (500~1000장)
    "medium": 10,   # 백본 고정 (100~500장) - 권장
    "heavy": 15,    # 더 많이 고정 (100장 이하)
}


class TrainService:
    def __init__(self) -> None:
        self._jobs: dict[str, dict[str, Any]] = {}
        self._jobs_lock = Lock()
        self._log_queues: dict[str, queue.Queue] = {}
        self._log_lock = Lock()

    def create_job(self) -> dict[str, Any]:
        job_id = str(uuid4())
        with self._jobs_lock:
            self._jobs[job_id] = {
                "jobId": job_id,
                "status": "RUNNING",
                "progress": 0,
                "message": "Training started",
            }
        with self._log_lock:
            self._log_queues[job_id] = queue.Queue()
        return self._jobs[job_id]

    def get_job(self, job_id: str) -> dict[str, Any]:
        with self._jobs_lock:
            job = self._jobs.get(job_id)
        if not job:
            return {"jobId": job_id, "status": "FAILED", "progress": 0, "message": "Job not found"}
        return job

    def stream_logs(self, job_id: str) -> Generator[str, None, None]:
        with self._log_lock:
            q = self._log_queues.get(job_id)
        if q is None:
            yield 'event: error\ndata: {"message":"job not found"}\n\n'
            return

        while True:
            try:
                event, payload = q.get(timeout=30)
                yield f"event: {event}\ndata: {json.dumps(payload)}\n\n"
                if event == "done":
                    break
            except queue.Empty:
                yield ": heartbeat\n\n"

    def run_yolo_training(self, job_id: str, req: TrainRequest) -> None:
        """
        Ultralytics YOLO 파인튜닝 실행.
        on_fit_epoch_end 콜백으로 epoch마다 메트릭을 SSE로 전송.
        """

        def emit(event: str, payload: dict[str, Any]) -> None:
            with self._log_lock:
                q = self._log_queues.get(job_id)
            if q:
                q.put((event, payload))

        try:
            epochs = max(req.epochs, 1)
            freeze = FREEZE_MAP.get(req.freezeLayers or "medium", 10)

            emit(
                "log",
                {
                    "line": (
                        f"[SYSTEM] Session initialized | "
                        f"model={BASE_MODEL_PATH} | epochs={epochs} | "
                        f"batch={req.batchSize} | lr={req.learningRate} | "
                        f"optimizer={req.optimizer} | freeze={freeze}"
                    ),
                },
            )

            emit("log", {"line": "[SYSTEM] Loading pretrained weights..."})
            model = YOLO(BASE_MODEL_PATH)

            def on_fit_epoch_end(trainer) -> None:
                with self._jobs_lock:
                    if self._jobs.get(job_id, {}).get("status") == "CANCELLED":
                        trainer.epoch = trainer.epochs
                        return

                epoch = trainer.epoch + 1
                total = trainer.epochs
                progress = int((epoch / total) * 100)

                metrics = trainer.metrics or {}
                val_box_loss = metrics.get("val/box_loss")
                map50 = metrics.get("metrics/mAP50(B)")
                map50_95 = metrics.get("metrics/mAP50-95(B)")
                lr_val = trainer.optimizer.param_groups[0]["lr"] if trainer.optimizer else req.learningRate
                tloss = float(trainer.tloss) if hasattr(trainer, "tloss") and trainer.tloss is not None else None

                parts = [f"[EPOCH {epoch:03d}/{total}]"]
                if tloss is not None:
                    parts.append(f"loss={tloss:.4f}")
                if val_box_loss is not None:
                    parts.append(f"val_loss={val_box_loss:.4f}")
                if map50 is not None:
                    parts.append(f"val_acc={map50:.4f}")
                if map50_95 is not None:
                    parts.append(f"mAP={map50_95:.4f}")
                parts.append(f"lr={lr_val:.2e}")

                with self._jobs_lock:
                    if job_id in self._jobs:
                        self._jobs[job_id]["progress"] = progress
                        self._jobs[job_id]["message"] = f"Epoch {epoch}/{total}"

                emit("log", {"line": " | ".join(parts), "progress": progress})

            def on_train_start(trainer) -> None:
                emit("log", {"line": f"[INFO] CUDA: {trainer.device} | imgsz={trainer.args.imgsz}"})

            model.add_callback("on_fit_epoch_end", on_fit_epoch_end)
            model.add_callback("on_train_start", on_train_start)

            emit("log", {"line": "[SYSTEM] Training started..."})
            model.train(
                data=DATASET_YAML_PATH,
                epochs=epochs,
                batch=req.batchSize,
                lr0=req.learningRate,
                optimizer=req.optimizer,
                freeze=freeze,
                single_cls=True,
                patience=20,
                project="runs/finetune",
                name=f"pill_{job_id[:8]}",
            )

            with self._jobs_lock:
                if job_id in self._jobs:
                    self._jobs[job_id]["status"] = "SUCCEEDED"
                    self._jobs[job_id]["progress"] = 100
                    self._jobs[job_id]["message"] = "Training complete"

            emit("log", {"line": f"[SYSTEM] Training complete. Best weights: runs/finetune/pill_{job_id[:8]}/weights/best.pt"})
            emit("done", {"status": "SUCCEEDED", "message": "Training complete"})
            notify_spring_completion(job_id, "SUCCEEDED", 100, "Training complete")

        except Exception as e:
            logger.exception("Training failed for job %s", job_id)
            with self._jobs_lock:
                if job_id in self._jobs:
                    self._jobs[job_id]["status"] = "FAILED"
                    self._jobs[job_id]["message"] = str(e)
            emit("log", {"line": f"[ERROR] {e}"})
            emit("done", {"status": "FAILED", "message": str(e)})
            notify_spring_completion(job_id, "FAILED", 0, str(e))


train_service = TrainService()
