import easyocr
import numpy as np
import cv2

class OCRService:
    def __init__(self):
        self.reader = easyocr.Reader(['ko', 'en'])

    async def process_ocr(self, file_content: bytes):
        nparr = np.frombuffer(file_content, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        results = self.reader.readtext(img)

        output = [
            {"text": res[1], "confidence": round(res[2], 2)}
            for res in results
        ]
        return output

ocr_service = OCRService()