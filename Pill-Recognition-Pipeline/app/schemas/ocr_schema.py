from pydantic import BaseModel
from typing import List

class OCRResult(BaseModel):
    text: str
    confidence: float


class OCRResponse(BaseModel):
    filename: str
    results: List[OCRResult]
    message: str = "Success"