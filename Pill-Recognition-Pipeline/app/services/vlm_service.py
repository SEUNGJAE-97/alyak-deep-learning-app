import json
import logging
import numpy as np
import cv2
import torch
from PIL import Image
from mlx_vlm import load, generate
from mlx_vlm.prompt_utils import apply_chat_template
from mlx_vlm.utils import load_config

logger = logging.getLogger(__name__)


# ── VLM 서비스 ─────────────────────────────────────────────

class VLMService:
    def __init__(self):
        model_id = "Qwen/Qwen2.5-VL-3B-Instruct"
        self.model, self.processor = load(model_id)
        self.config = load_config(model_id)

    def identify_pill(self, image: Image.Image) -> dict:
        """PIL 이미지 → VLM 추론 → JSON(shape, print, color) 반환"""
        prompt_text = (
            "Look at this pill image carefully.\n"
            "Return ONLY a JSON object with these exact keys:\n"
            "- shape: one of [round, oval, oblong, capsule, triangle, rectangle, diamond, pentagon, hexagon, octagon, other]\n"
            "- print: ALL text imprinted on this pill, concatenated with space (empty string if none)\n"
            "- color: one of [white, yellow, orange, pink, red, brown, light_green, green, teal, blue, navy, purple, gray, black, transparent, other]\n"
            "No explanation. Output JSON only."
        )

        prompt = apply_chat_template(
            self.processor,
            self.config,
            prompt_text,
            num_images=1,
        )

        result = generate(
            self.model,
            self.processor,   
            prompt,
            image,   
            max_tokens=64,
            verbose=False,
        )
        text = result.text
        start = text.find("{")
        end = text.rfind("}") + 1
        if start == -1 or end == 0:
            logger.warning(f"VLM JSON 파싱 실패: {text}")
            return {"parse_error": True, "raw": text}
        try:
            return json.loads(text[start:end])
        except json.JSONDecodeError:
            logger.warning(f"VLM JSON 디코딩 실패: {text}")
            return {"parse_error": True, "raw": text}


# ── OCR 서비스 (VLM으로 대체) ──────────────────────────────

class OCRService:
    def __init__(self):
        self.vlm = VLMService()

    def cv2_to_pil(self, img: np.ndarray) -> Image.Image:
        """cv2 BGR → PIL RGB 변환"""
        rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        return Image.fromarray(rgb)

    async def process_ocr(self, file_content: bytes) -> dict:
        nparr = np.frombuffer(file_content, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        if img is None:
            raise ValueError("이미지를 디코딩할 수 없습니다.")

        pil_image = self.cv2_to_pil(img)
        vlm_result = self.vlm.identify_pill(pil_image)
        logger.info(f"VLM 결과: {vlm_result}")

        if vlm_result.get("parse_error"):
            return {"shape": None, "texts": [], "color": "other"}

        return {
            "shape": vlm_result.get("shape"),
            "texts": vlm_result.get("print", "").split(),
            "color": vlm_result.get("color", "other"),
        }


ocr_service = OCRService()