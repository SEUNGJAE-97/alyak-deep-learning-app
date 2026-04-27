"""
Build YOLO dataset files from Spring TRAINING_SET items.

- Fetch image list/details from internal Spring endpoints.
- Generate YOLO label .txt files under shared storage.
- Generate a temporary data.yaml for one training job.
"""

import logging
import os
import random
from pathlib import Path

import httpx
import yaml

logger = logging.getLogger(__name__)

SPRING_BASE_URL = os.getenv("SPRING_BASE_URL", "http://alyak-api:8080")
SPRING_INTERNAL_TOKEN = os.getenv("TRAINING_CALLBACK_TOKEN", "local-training-callback-token")
SHARED_IMAGE_BASE = os.getenv("SHARED_IMAGE_BASE", "/app/shared-images/uploads")
SHARED_LABEL_BASE = os.getenv("SHARED_LABEL_BASE", "/app/shared-images/labels")


def _headers() -> dict[str, str]:
    return {"X-Internal-Token": SPRING_INTERNAL_TOKEN}


def _fetch_training_items() -> list[dict]:
    """Fetch all TRAINING_SET items with pagination."""
    all_items: list[dict] = []
    page = 0

    while True:
        resp = httpx.get(
            f"{SPRING_BASE_URL}/api/internal/labeling/items",
            headers=_headers(),
            params={"status": "TRAINING_SET", "page": page, "pageSize": 100},
            timeout=30,
        )
        resp.raise_for_status()
        data = resp.json()
        items = data.get("content", [])
        all_items.extend(items)

        if data.get("last", True) or not items:
            break
        page += 1

    logger.info("Fetched %d TRAINING_SET items", len(all_items))
    return all_items


def _fetch_item_detail(item_id: int) -> dict | None:
    try:
        resp = httpx.get(
            f"{SPRING_BASE_URL}/api/internal/labeling/items/{item_id}",
            headers=_headers(),
            timeout=30,
        )
        resp.raise_for_status()
        return resp.json()
    except Exception as exc:
        logger.warning("Failed to fetch detail for item %s: %s", item_id, exc)
        return None


def _convert_to_yolo(box: dict) -> str:
    x_min = float(box["xMin"])
    y_min = float(box["yMin"])
    x_max = float(box["xMax"])
    y_max = float(box["yMax"])

    x_center = (x_min + x_max) / 2
    y_center = (y_min + y_max) / 2
    width = x_max - x_min
    height = y_max - y_min
    return f"0 {x_center:.6f} {y_center:.6f} {width:.6f} {height:.6f}"


def _write_label_file(item_detail: dict) -> str | None:
    image_path = item_detail.get("imagePath", "")
    boxes = item_detail.get("boxes", [])
    if not boxes:
        logger.warning("No boxes for item %s, skipping", item_detail.get("id"))
        return None

    filename_stem = Path(image_path).stem
    os.makedirs(SHARED_LABEL_BASE, exist_ok=True)
    label_path = os.path.join(SHARED_LABEL_BASE, f"{filename_stem}.txt")

    lines = [_convert_to_yolo(box) for box in boxes]
    with open(label_path, "w", encoding="utf-8") as file:
        file.write("\n".join(lines))
    return label_path


def _resolve_image_abs_path(web_image_path: str) -> str:
    """
    Convert web path to mounted filesystem path.
    Example:
      /uploads/pill-images/a.jpg -> /app/shared-images/uploads/pill-images/a.jpg
    """
    web_path = (web_image_path or "").strip()
    if web_path.startswith("/uploads/"):
        rel_path = web_path[len("/uploads/") :]
    else:
        rel_path = Path(web_path).name
    return os.path.join(SHARED_IMAGE_BASE, rel_path)


def prepare_dataset(job_id: str, val_ratio: float = 0.2) -> str:
    items = _fetch_training_items()
    if not items:
        raise ValueError("TRAINING_SET 이미지가 없습니다. 학습을 시작할 수 없습니다.")

    valid_items: list[dict] = []
    for item in items:
        detail = _fetch_item_detail(item["id"])
        if not detail:
            continue

        label_path = _write_label_file(detail)
        if label_path is None:
            continue

        image_abs_path = _resolve_image_abs_path(detail.get("imagePath", ""))
        if not os.path.exists(image_abs_path):
            logger.warning("Image file not found: %s", image_abs_path)
            continue

        valid_items.append({"image_path": image_abs_path, "label_path": label_path})

    if not valid_items:
        raise ValueError("유효한 학습 이미지(이미지+라벨 모두 존재)가 없습니다.")

    logger.info("Valid items: %d / %d", len(valid_items), len(items))

    random.shuffle(valid_items)
    split_idx = max(1, int(len(valid_items) * (1 - val_ratio)))
    train_items = valid_items[:split_idx]
    val_items = valid_items[split_idx:] if len(valid_items) > 1 else valid_items[:1]

    yaml_dir = f"/tmp/datasets/{job_id}"
    os.makedirs(yaml_dir, exist_ok=True)
    yaml_path = f"{yaml_dir}/data.yaml"

    data_yaml = {
        "train": [item["image_path"] for item in train_items],
        "val": [item["image_path"] for item in val_items],
        "nc": 1,
        "names": ["pill"],
    }
    with open(yaml_path, "w", encoding="utf-8") as file:
        yaml.dump(data_yaml, file, default_flow_style=False, allow_unicode=True)

    logger.info("Dataset ready | train=%d val=%d | yaml=%s", len(train_items), len(val_items), yaml_path)
    return yaml_path


def cleanup_dataset(job_id: str) -> None:
    import shutil

    yaml_dir = f"/tmp/datasets/{job_id}"
    if os.path.exists(yaml_dir):
        shutil.rmtree(yaml_dir)
        logger.info("Cleaned up temp dataset dir for job %s", job_id)
