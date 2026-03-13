from fastapi import APIRouter, UploadFile, File, Depends
from app.services.ocr_service import ocr_service
from app.schemas.ocr_schema import OCRResponse
from typing import List
router = APIRouter()

@router.post("/process", response_model=OCRResponse)
async def perform_ocr(images: List[UploadFile] = File(...)) :
   content = await images.read()
   ocr_results = await ocr_service.process_ocr(content)

   return OCRResponse(
       filename=images.filename,
       results=ocr_results
   )