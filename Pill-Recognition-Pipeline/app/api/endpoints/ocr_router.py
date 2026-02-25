from fastapi import APIRouter, UploadFile, File, Depends
from app.services.ocr_service import ocr_service
from app.schemas.ocr_schema import OCRResponse

router = APIRouter()

@router.post("/process", response_model=OCRResponse)
async def perform_ocr(file: UploadFile = File(...)) :
   content = await file.read()
   ocr_results = await ocr_service.process_ocr(content)

   return OCRResponse(
       filename=file.filename,
       results=ocr_results
   )