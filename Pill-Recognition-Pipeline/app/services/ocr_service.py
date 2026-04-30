import logging
import os
import uuid
import easyocr
import numpy as np
import cv2

logger = logging.getLogger(__name__)


def deduplicate_results(results: list, iou_threshold: float = 0.5) -> list:
    seen_texts = {}
    for res in results:
        text = res["text"].strip().upper()
        if not text:
            continue
        # 같은 텍스트면 confidence 높은 것만 유지
        if text not in seen_texts or res["confidence"] > seen_texts[text]["confidence"]:
            seen_texts[text] = res
    return list(seen_texts.values())


class OCRService:
    def __init__(self):
        self.reader = easyocr.Reader(['ko', 'en'])
        self.confidence_threshold = 0.5
        # docker-compose에서 /app/shared-images가 호스트와 마운트됨
        self.debug_dir = os.getenv("OCR_DEBUG_DIR", "/app/shared-images/debug_images")

    def preprocess_pill_image(self, img: np.ndarray, debug: bool = True, debug_prefix: str = "debug") -> np.ndarray:
        if debug:
            os.makedirs(self.debug_dir, exist_ok=True)

        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        if debug:
            cv2.imwrite(os.path.join(self.debug_dir, f"{debug_prefix}_1_gray.jpg"), gray)

        h, w = gray.shape[:2]
        if min(h, w) < 500:
            gray = cv2.resize(gray, (w * 2, h * 2), interpolation=cv2.INTER_CUBIC)

        clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8, 8))
        enhanced = clahe.apply(gray)

        if debug:
            cv2.imwrite(os.path.join(self.debug_dir, f"{debug_prefix}_2_clahe.jpg"), enhanced)

        blurred = cv2.GaussianBlur(enhanced, (0, 0), 3)
        sharpened = cv2.addWeighted(enhanced, 1.5, blurred, -0.5, 0)

        if debug:
            cv2.imwrite(os.path.join(self.debug_dir, f"{debug_prefix}_3_sharpened.jpg"), sharpened)

        binary = cv2.adaptiveThreshold(
            sharpened, 255,
            cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
            cv2.THRESH_BINARY,
            11, 2
        )

        if debug:
            cv2.imwrite(os.path.join(self.debug_dir, f"{debug_prefix}_4_binary.jpg"), binary)

        return binary

    async def process_ocr(self, file_content: bytes) -> list:
        nparr = np.frombuffer(file_content, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        if img is None:
            raise ValueError("이미지를 디코딩할 수 없습니다.")

        debug_prefix = f"ocr_{uuid.uuid4().hex[:8]}"
        # 실제 OCR 요청 경로에서 디버그 전처리 이미지 저장
        preprocessed = self.preprocess_pill_image(img, debug=True, debug_prefix=debug_prefix)
        logger.info("OCR debug images saved: dir=%s prefix=%s", self.debug_dir, debug_prefix)

        inverted = cv2.bitwise_not(preprocessed)

        ocr_params = {
            "detail": 1,
            "paragraph": False,
            "contrast_ths": 0.3,
            "adjust_contrast": 0.7,
            "text_threshold": 0.5,
            "low_text": 0.3,
            "width_ths": 0.7,
            "add_margin": 0.1,
        }

        results_normal = self.reader.readtext(preprocessed, **ocr_params)
        results_inverted = self.reader.readtext(inverted, **ocr_params)

        logger.info(f"정방향: {results_normal}")
        logger.info(f"반전: {results_inverted}")

        all_results = []
        for res in results_normal + results_inverted:
            text, confidence = res[1], res[2]
            if confidence < self.confidence_threshold:
                continue
            all_results.append({"text": text, "confidence": round(confidence, 2)})

        output = deduplicate_results(all_results)
        output.sort(key=lambda x: x["confidence"], reverse=True)
        return output


# OCRService 인스턴스 생성
ocr_service = OCRService()