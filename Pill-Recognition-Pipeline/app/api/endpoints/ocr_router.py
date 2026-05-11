from fastapi import APIRouter, UploadFile, File
from app.services.ocr_service import ocr_service
from app.schemas.ocr_schema import OCRResponse
from typing import List
import logging
logger = logging.getLogger(__name__)

router = APIRouter()

@router.post("/process", response_model=OCRResponse)
async def perform_ocr(images: List[UploadFile] = File(...)):
    if not images:
        raise ValueError("이미지가 없습니다.")
    file = images[0]
    content = await file.read()
    ocr_results = await ocr_service.process_ocr(content)
    logger.info(f"OCR results: {ocr_results}")
    return OCRResponse(**ocr_results)