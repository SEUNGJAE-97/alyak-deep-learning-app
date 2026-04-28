from fastapi import APIRouter, BackgroundTasks
from fastapi.responses import StreamingResponse

from app.schemas.training_schema import TrainRequest
from app.services.train_service import train_service

router = APIRouter()


@router.post("/train/jobs")
def start_training(req: TrainRequest, background_tasks: BackgroundTasks):
    job = train_service.create_job()
    background_tasks.add_task(train_service.run_yolo_training, job["jobId"], req)
    return job


@router.get("/train/jobs/{job_id}")
def get_training_job(job_id: str):
    return train_service.get_job(job_id)


@router.get("/train/system/status")
def get_training_system_status():
    return train_service.get_system_status()


@router.get("/train/jobs/{job_id}/logs/stream")
def stream_training_logs(job_id: str):
    return StreamingResponse(train_service.stream_logs(job_id), media_type="text/event-stream")
