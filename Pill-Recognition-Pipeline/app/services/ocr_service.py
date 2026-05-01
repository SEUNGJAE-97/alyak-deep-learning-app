import json
import logging
import numpy as np
import cv2
import torch
from PIL import Image
from transformers import Qwen2_5_VLForConditionalGeneration, AutoProcessor

logger = logging.getLogger(__name__)


# ── VLM 서비스 ─────────────────────────────────────────────

class VLMService:
    def __init__(self):
        model_id = "Qwen/Qwen2.5-VL-3B-Instruct"

        if torch.backends.mps.is_available():
            self.device = torch.device("mps")
        elif torch.cuda.is_available():
            self.device = torch.device("cuda")
        else:
            self.device = torch.device("cpu")

        logger.info(f"VLM 로드 중: {model_id} / device={self.device}")
        # mps에서는 float16 + 단일 디바이스 고정이 지연을 줄이는 편
        torch_dtype = torch.float16 if self.device.type in ("mps", "cuda") else torch.float32
        device_map = None if self.device.type in ("mps", "cuda") else "auto"
        self.model = Qwen2_5_VLForConditionalGeneration.from_pretrained(
            model_id,
            torch_dtype=torch_dtype,
            device_map=device_map,
            low_cpu_mem_usage=True,
            ignore_mismatched_sizes=True,
        ).to(self.device)
        self.processor = AutoProcessor.from_pretrained(model_id)
        logger.info("VLM 로드 완료")

    def identify_pill(self, image: Image.Image) -> dict:
        """PIL 이미지 → VLM 추론 → JSON(shape, print, color) 반환"""
        messages = [{
            "role": "user",
            "content": [
                {"type": "image", "image": image},
                {"type": "text", "text": (
                    "Look at this pill image carefully.\n"
                    "Return ONLY a JSON object with these exact keys:\n"
                    "- shape: one of [round, oval, oblong, capsule, triangle, rectangle, diamond, pentagon, hexagon, octagon, other]\n"
                    "- print: ALL text imprinted on this pill, concatenated with space (empty string if none)\n"
                    "- color: one of [white, yellow, orange, pink, red, brown, light_green, green, teal, blue, navy, purple, gray, black, transparent, other]\n"
                    "No explanation. Output JSON only."
                )}
            ]
        }]

        text = self.processor.apply_chat_template(
            messages, tokenize=False, add_generation_prompt=True
        )
        inputs = self.processor(
            text=[text], images=[image], return_tensors="pt"
        ).to(self.device)

        with torch.no_grad():
            output = self.model.generate(**inputs, max_new_tokens=64, do_sample=False)

        result = self.processor.decode(output[0], skip_special_tokens=True)

        start = result.find("{")
        end = result.rfind("}") + 1
        if start == -1 or end == 0:
            logger.warning(f"VLM JSON 파싱 실패: {result}")
            return {"parse_error": True, "raw": result}
        try:
            return json.loads(result[start:end])
        except json.JSONDecodeError:
            logger.warning(f"VLM JSON 디코딩 실패: {result}")
            return {"parse_error": True, "raw": result}


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