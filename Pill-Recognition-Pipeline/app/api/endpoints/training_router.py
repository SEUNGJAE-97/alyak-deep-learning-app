from fastapi import APIRouter, BackgroundTasks
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from uuid import uuid4
from threading import Lock
import queue
import time
import json
import os
import logging
import urllib.request
import urllib.error

router = APIRouter()
logger = logging.getLogger(__name__)

_jobs: dict[str, dict] = {}
_jobs_lock = Lock()
_log_queues: dict[str, queue.Queue] = {}
_log_lock = Lock()

SPRING_CALLBACK_BASE_URL = os.getenv("SPRING_CALLBACK_BASE_URL", "http://alyak-api:8080")
SPRING_CALLBACK_TOKEN = os.getenv("TRAINING_CALLBACK_TOKEN", "local-training-callback-token")


class TrainRequest(BaseModel):
    datasetStatus: str
    epochs: int
    batchSize: int
    learningRate: float
    optimizer: str
    freezeLayers: str | None = None


def _notify_spring_completion(job_id: str, status: str, progress: int, message: str) -> None:
    callback_url = f"{SPRING_CALLBACK_BASE_URL}/api/internal/training/jobs/{job_id}/complete"
    payload = {
        "status": status,
        "progress": progress,
        "message": message,
    }
    req = urllib.request.Request(
        callback_url,
        data=json.dumps(payload).encode("utf-8"),
        method="PATCH",
        headers={
            "Content-Type": "application/json",
            "X-Internal-Token": SPRING_CALLBACK_TOKEN,
        },
    )
    try:
        with urllib.request.urlopen(req, timeout=5) as response:
            if response.status >= 300:
                logger.warning("Spring callback non-success status: %s", response.status)
    except urllib.error.HTTPError as e:
        logger.warning("Spring callback failed with HTTPError: %s", e)
    except Exception as e:
        logger.warning("Spring callback failed: %s", e)


def _run_training(job_id: str, req: TrainRequest) -> None:
    def emit(event: str, payload: dict) -> None:
        with _log_lock:
            q = _log_queues.get(job_id)
        if q:
            q.put((event, payload))

    try:
        epochs = max(req.epochs, 1)
        emit("log", {"line": f"Training session started. epochs={epochs}"})
        for epoch in range(1, epochs + 1):
            # Simulated training step; replace with real pipeline.
            time.sleep(0.2)
            progress = int((epoch / epochs) * 100)
            with _jobs_lock:
                if job_id not in _jobs:
                    return
                if _jobs[job_id]["status"] == "CANCELLED":
                    emit("done", {"status": "CANCELLED", "message": "Training cancelled"})
                    _notify_spring_completion(job_id, "CANCELLED", progress, "Training cancelled")
                    return
                _jobs[job_id]["progress"] = progress
                _jobs[job_id]["message"] = f"Epoch {epoch}/{epochs}"
            emit("progress", {"progress": progress, "status": "RUNNING"})
            emit("log", {"line": f"Epoch {epoch}/{epochs} complete"})

        with _jobs_lock:
            if job_id in _jobs:
                _jobs[job_id]["status"] = "SUCCEEDED"
                _jobs[job_id]["progress"] = 100
                _jobs[job_id]["message"] = "Training complete"
        emit("done", {"status": "SUCCEEDED", "message": "Training complete"})
        _notify_spring_completion(job_id, "SUCCEEDED", 100, "Training complete")
    except Exception as e:
        with _jobs_lock:
            if job_id in _jobs:
                _jobs[job_id]["status"] = "FAILED"
                _jobs[job_id]["message"] = str(e)
        emit("error", {"message": str(e)})
        emit("done", {"status": "FAILED", "message": str(e)})
        _notify_spring_completion(job_id, "FAILED", 0, str(e))


@router.post("/train/jobs")
def start_training(req: TrainRequest, background_tasks: BackgroundTasks):
    job_id = str(uuid4())
    with _jobs_lock:
        _jobs[job_id] = {
            "jobId": job_id,
            "status": "RUNNING",
            "progress": 0,
            "message": "Training started",
        }
    with _log_lock:
        _log_queues[job_id] = queue.Queue()
    background_tasks.add_task(_run_training, job_id, req)
    return _jobs[job_id]


@router.get("/train/jobs/{job_id}")
def get_training_job(job_id: str):
    with _jobs_lock:
        job = _jobs.get(job_id)
    if not job:
        return {
            "jobId": job_id,
            "status": "FAILED",
            "progress": 0,
            "message": "Job not found",
        }
    return job


@router.get("/train/jobs/{job_id}/logs/stream")
def stream_training_logs(job_id: str):
    def event_generator():
        with _log_lock:
            q = _log_queues.get(job_id)
        if q is None:
            yield "event: error\ndata: {\"message\":\"job not found\"}\n\n"
            return

        while True:
            try:
                event, payload = q.get(timeout=30)
                yield f"event: {event}\ndata: {json.dumps(payload)}\n\n"
                if event == "done":
                    break
            except queue.Empty:
                yield ": heartbeat\n\n"

    return StreamingResponse(event_generator(), media_type="text/event-stream")
