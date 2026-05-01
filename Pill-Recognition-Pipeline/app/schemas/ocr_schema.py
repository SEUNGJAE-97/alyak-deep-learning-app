from pydantic import BaseModel
from typing import List, Optional


class OCRResponse(BaseModel):
    shape: Optional[str]
    texts: List[str]
    color: Optional[str] = None