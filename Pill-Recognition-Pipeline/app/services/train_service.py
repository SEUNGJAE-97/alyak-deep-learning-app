import json
import logging
import os
import queue
import re
import time
from threading import Lock
from typing import Any, Generator
from uuid import uuid4

from ultralytics import YOLO

from app.schemas.training_schema import TrainRequest
from app.services.dataset_builder import cleanup_dataset, prepare_dataset
from app.utils.callback import notify_spring_completion

logger = logging.getLogger(__name__)

# 학습된 모델 경로
BASE_MODEL_PATH = os.getenv("BASE_MODEL_PATH", "models/pill_trained.pt")
TRAINING_RUNS_ROOT = os.getenv("TRAINING_RUNS_ROOT", "/app/shared-images/model-runs")

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
        self._timing_by_job: dict[str, dict[str, float]] = {}

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

    def _emit(self, job_id: str, event: str, payload: dict[str, Any]) -> None:
        with self._log_lock:
            q = self._log_queues.get(job_id)
        if q:
            q.put((event, payload))

    def _is_cancelled(self, job_id: str) -> bool:
        with self._jobs_lock:
            return self._jobs.get(job_id, {}).get("status") == "CANCELLED"

    def _update_job_progress(self, job_id: str, progress: int, message: str) -> None:
        with self._jobs_lock:
            if job_id in self._jobs:
                self._jobs[job_id]["progress"] = progress
                self._jobs[job_id]["message"] = message

    def _mark_job_succeeded(self, job_id: str) -> None:
        with self._jobs_lock:
            if job_id in self._jobs:
                self._jobs[job_id]["status"] = "SUCCEEDED"
                self._jobs[job_id]["progress"] = 100
                self._jobs[job_id]["message"] = "Training complete"

    def _mark_job_failed(self, job_id: str, error_message: str) -> None:
        with self._jobs_lock:
            if job_id in self._jobs:
                self._jobs[job_id]["status"] = "FAILED"
                self._jobs[job_id]["message"] = error_message

    def _to_user_friendly_error(self, error: Exception) -> str:
        if isinstance(error, FileNotFoundError):
            missing_path = getattr(error, "filename", None) or ""
            if missing_path.endswith(".pt") or "pill_trained.pt" in missing_path:
                return "선택한 베이스 모델 파일을 찾을 수 없습니다. 모델 경로를 확인해 주세요."
            return "학습에 필요한 파일을 찾을 수 없습니다. 경로 설정을 확인해 주세요."

        raw_message = str(error)
        if "TRAINING_SET 이미지가 없습니다" in raw_message:
            return "학습에 사용할 이미지가 없습니다. Training Set을 먼저 준비해 주세요."
        if "유효한 학습 이미지" in raw_message:
            return "학습 가능한 이미지가 없습니다. 라벨 박스와 이미지 파일 상태를 확인해 주세요."
        if "403" in raw_message or "forbidden" in raw_message.lower():
            return "내부 인증에 실패했습니다. 서버 토큰 설정을 확인해 주세요."
        if "httpx" in raw_message.lower() or "connect" in raw_message.lower() or "timeout" in raw_message.lower():
            return "서버 연결에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요."

        return "학습 중 오류가 발생했습니다. 서버 로그를 확인해 주세요."

    def _build_epoch_log_line(self, epoch: int, total: int, trainer, req: TrainRequest, eta_seconds: int | None = None) -> str:
        metrics = trainer.metrics or {}
        val_box_loss = metrics.get("val/box_loss")
        map50 = metrics.get("metrics/mAP50(B)")
        map50_95 = metrics.get("metrics/mAP50-95(B)")
        lr_val = trainer.optimizer.param_groups[0]["lr"] if trainer.optimizer else req.learningRate
        tloss = None
        if hasattr(trainer, "tloss") and trainer.tloss is not None:
            try:
                if hasattr(trainer.tloss, "mean") and hasattr(trainer.tloss, "item"):
                    tloss = float(trainer.tloss.mean().item())
                else:
                    tloss = float(trainer.tloss)
            except Exception:
                tloss = None

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
        if eta_seconds is not None:
            eta_seconds = max(0, int(eta_seconds))
            h = eta_seconds // 3600
            m = (eta_seconds % 3600) // 60
            s = eta_seconds % 60
            parts.append(f"eta={h:02d}:{m:02d}:{s:02d}")
        return " | ".join(parts)

    def _build_run_name(self, job_id: str) -> str:
        """
        model-runs 디렉토리에서 기존 pill_detection_vN을 스캔해
        다음 버전명을 생성한다.
        """
        prefix = "pill_detection_v"
        max_version = 0

        try:
            os.makedirs(TRAINING_RUNS_ROOT, exist_ok=True)
            pattern = re.compile(rf"^{re.escape(prefix)}(\d+)$")
            for entry in os.listdir(TRAINING_RUNS_ROOT):
                match = pattern.match(entry)
                if match:
                    max_version = max(max_version, int(match.group(1)))
        except Exception:
            # 스캔에 실패하면 충돌 가능성이 낮은 job_id fallback 사용
            safe_job_id = (job_id or "").replace("/", "_").replace("\\", "_")
            return f"pill_{safe_job_id}"

        return f"{prefix}{max_version + 1}"

    def _on_fit_epoch_end(self, job_id: str, req: TrainRequest, trainer) -> None:
        if self._is_cancelled(job_id):
            trainer.epoch = trainer.epochs
            return

        epoch = trainer.epoch + 1
        total = trainer.epochs
        progress = int((epoch / total) * 100)
        timing = self._timing_by_job.get(job_id)
        eta_seconds = None
        if timing is not None:
            elapsed = max(0.0, time.monotonic() - timing["started_at"])
            avg_per_epoch = elapsed / epoch if epoch > 0 else 0.0
            remaining_epochs = max(0, total - epoch)
            eta_seconds = int(avg_per_epoch * remaining_epochs)

        self._update_job_progress(job_id, progress, f"Epoch {epoch}/{total}")
        log_line = self._build_epoch_log_line(epoch, total, trainer, req, eta_seconds)
        self._emit(job_id, "log", {"line": log_line, "progress": progress})

    def _on_train_start(self, job_id: str, trainer) -> None:
        self._emit(job_id, "log", {"line": "[SYSTEM] 모델 학습 중..."})

    def run_yolo_training(self, job_id: str, req: TrainRequest) -> None:
        """
        Ultralytics YOLO 파인튜닝 실행.
        on_fit_epoch_end 콜백으로 epoch마다 메트릭을 SSE로 전송.
        """

        try:
            epochs = max(req.epochs, 1)
            freeze = FREEZE_MAP.get(req.freezeLayers or "medium", 10)
            dataset_yaml_path = prepare_dataset(job_id)
            run_name = self._build_run_name(job_id)
            base_model_path = req.baseModelPath or BASE_MODEL_PATH
            self._timing_by_job[job_id] = {"started_at": time.monotonic()}

            self._emit(
                job_id,
                "log",
                {
                    "line": (
                        f"[SYSTEM] Session initialized | "
                        f"model={base_model_path} | epochs={epochs} | "
                        f"batch={req.batchSize} | lr={req.learningRate} | "
                        f"optimizer={req.optimizer} | freeze={freeze}"
                    ),
                },
            )

            self._emit(job_id, "log", {"line": "[SYSTEM] Loading pretrained weights..."})
            model = YOLO(base_model_path)

            def on_fit_epoch_end(trainer) -> None:
                self._on_fit_epoch_end(job_id, req, trainer)

            def on_train_start(trainer) -> None:
                self._on_train_start(job_id, trainer)

            # 콜백함수 등록
            model.add_callback("on_fit_epoch_end", on_fit_epoch_end)
            model.add_callback("on_train_start", on_train_start)

            # 학습 시작
            self._emit(job_id, "log", {"line": "[SYSTEM] Training started..."})
            model.train(
                data=dataset_yaml_path,
                epochs=epochs,
                batch=req.batchSize,
                lr0=req.learningRate,
                optimizer=req.optimizer,
                freeze=freeze,
                single_cls=True,
                patience=20,
                project=TRAINING_RUNS_ROOT,
                name=run_name,
            )

            self._mark_job_succeeded(job_id)
            self._emit(
                job_id,
                "log",
                {"line": f"[SYSTEM] Training complete. Best weights: {TRAINING_RUNS_ROOT}/{run_name}/weights/best.pt"},
            )
            self._emit(job_id, "done", {"status": "SUCCEEDED", "message": "Training complete"})
            notify_spring_completion(job_id, "SUCCEEDED", 100, "Training complete")

        except Exception as e:
            logger.exception("Training failed for job %s", job_id)
            user_message = self._to_user_friendly_error(e)
            self._mark_job_failed(job_id, user_message)
            self._emit(job_id, "log", {"line": f"[ERROR] {user_message}"})
            self._emit(job_id, "done", {"status": "FAILED", "message": user_message})
            notify_spring_completion(job_id, "FAILED", 0, user_message)
        finally:
            self._timing_by_job.pop(job_id, None)
            cleanup_dataset(job_id)


train_service = TrainService()
