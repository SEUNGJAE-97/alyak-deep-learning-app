from fastapi import FastAPI
from app.api.endpoints.ocr_router import router as ocr_router

app = FastAPI(title="EasyOCR API")

app.include_router(ocr_router, prefix="/api/v1")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
