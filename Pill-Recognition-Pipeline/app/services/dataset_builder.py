"""
Build YOLO dataset files from Spring training snapshots.

- Fetch snapshot rows for a Spring training job id.
- Generate YOLO label .txt files under shared storage.
- Generate a temporary data.yaml for one training job.
"""

import json
import logging
import os
import random
import shutil
from collections import defaultdict
from pathlib import Path

import httpx
import yaml

import app.settings as settings

logger = logging.getLogger(__name__)


def _headers() -> dict[str, str]:
    return {"X-Internal-Token": settings.TRAINING_CALLBACK_TOKEN}


def _fetch_label_json_path(job_id: str) -> str:
    """Fetch the label JSON file path for a training job UUID."""
    resp = httpx.get(
        f"{settings.SPRING_BASE_URL}/api/internal/training/jobs/training-snapshot/{job_id}",
        headers=_headers(),
        timeout=30,
    )
    resp.raise_for_status()
    data = resp.json()
    return data.get("labelJsonPath") or data.get("label_json_path")


def _load_label_json(label_json_path: str) -> list[dict]:
    """Load training snapshot rows from a JSON file path."""
    if not label_json_path:
        raise ValueError("labelJsonPath가 비어 있습니다.")

    with open(label_json_path, "r", encoding="utf-8") as file:
        payload = json.load(file)

    items = payload.get("items", [])
    if not isinstance(items, list):
        raise ValueError("라벨 JSON의 items 형식이 올바르지 않습니다.")

    return items


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


def _write_label_file(image_path: str, boxes: list[dict]) -> str | None:
    if not boxes:
        logger.warning("No boxes for image %s, skipping", image_path)
        return None

    filename_stem = Path(image_path).stem
    os.makedirs(settings.SHARED_LABEL_BASE, exist_ok=True)
    label_path = os.path.join(settings.SHARED_LABEL_BASE, f"{filename_stem}.txt")

    lines = [_convert_to_yolo(box) for box in boxes]
    with open(label_path, "w", encoding="utf-8") as file:
        file.write("\n".join(lines))
    return label_path


def _safe_copy(src: str, dst: str) -> None:
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    shutil.copy2(src, dst)


def _resolve_image_abs_path(web_image_path: str) -> str:
    """
    Convert web path to mounted filesystem path.
    Example:
      /uploads/pill-images/a.jpg -> ../shared-images/uploads/pill-images/a.jpg
    """
    web_path = (web_image_path or "").strip()
    if web_path.startswith("/uploads/"):
        rel_path = web_path[len("/uploads/") :]
    else:
        rel_path = Path(web_path).name
    return os.path.join(settings.SHARED_IMAGE_BASE, rel_path)


def prepare_dataset(job_id: str, val_ratio: float = 0.2) -> str:
    label_json_path = _fetch_label_json_path(job_id)
    if not label_json_path:
        raise ValueError("라벨 JSON 경로를 찾을 수 없습니다.")

    label_items = _load_label_json(label_json_path)
    if not label_items:
        raise ValueError("학습 스냅샷이 없습니다. TRAINING_SET 데이터를 확인하세요.")

    snapshots: list[dict] = []
    for item in label_items:
        image_path = item.get("imagePath")
        boxes = item.get("boxes", [])
        if not image_path or not isinstance(boxes, list):
            continue
        for box in boxes:
            row = dict(box)
            row["imagePath"] = image_path
            snapshots.append(row)

    if not snapshots:
        raise ValueError("학습 스냅샷이 없습니다. TRAINING_SET 데이터를 확인하세요.")

    boxes_by_image: dict[str, list[dict]] = defaultdict(list)
    for row in snapshots:
        image_path = row.get("imagePath")
        if not image_path:
            continue
        boxes_by_image[image_path].append(row)

    valid_items: list[dict] = []
    for image_path, boxes in boxes_by_image.items():
        label_path = _write_label_file(image_path, boxes)
        if label_path is None:
            continue

        image_abs_path = _resolve_image_abs_path(image_path)
        if not os.path.exists(image_abs_path):
            logger.warning("Image file not found: %s", image_abs_path)
            continue

        valid_items.append({"image_path": image_abs_path, "label_path": label_path})

    if not valid_items:
        raise ValueError("유효한 학습 이미지(이미지+라벨 모두 존재)가 없습니다.")

    logger.info("Valid items: %d / %d images", len(valid_items), len(boxes_by_image))

    random.shuffle(valid_items)
    split_idx = max(1, int(len(valid_items) * (1 - val_ratio)))
    train_items = valid_items[:split_idx]
    val_items = valid_items[split_idx:] if len(valid_items) > 1 else valid_items[:1]

    dataset_root = f"/tmp/datasets/{job_id}"
    train_img_dir = f"{dataset_root}/images/train"
    val_img_dir = f"{dataset_root}/images/val"
    train_lbl_dir = f"{dataset_root}/labels/train"
    val_lbl_dir = f"{dataset_root}/labels/val"
    os.makedirs(train_img_dir, exist_ok=True)
    os.makedirs(val_img_dir, exist_ok=True)
    os.makedirs(train_lbl_dir, exist_ok=True)
    os.makedirs(val_lbl_dir, exist_ok=True)

    train_txt_path = f"{dataset_root}/train.txt"
    val_txt_path = f"{dataset_root}/val.txt"
    yaml_path = f"{dataset_root}/data.yaml"

    def materialize_split(split_items: list[dict], img_dir: str, lbl_dir: str) -> list[str]:
        image_paths: list[str] = []
        for item in split_items:
            src_img = item["image_path"]
            src_lbl = item["label_path"]
            file_name = os.path.basename(src_img)
            stem = Path(file_name).stem
            dst_img = os.path.join(img_dir, file_name)
            dst_lbl = os.path.join(lbl_dir, f"{stem}.txt")
            _safe_copy(src_img, dst_img)
            _safe_copy(src_lbl, dst_lbl)
            image_paths.append(dst_img)
        return image_paths

    train_image_paths = materialize_split(train_items, train_img_dir, train_lbl_dir)
    val_image_paths = materialize_split(val_items, val_img_dir, val_lbl_dir)

    with open(train_txt_path, "w", encoding="utf-8") as file:
        file.write("\n".join(train_image_paths))
    with open(val_txt_path, "w", encoding="utf-8") as file:
        file.write("\n".join(val_image_paths))

    data_yaml = {
        "train": train_txt_path,
        "val": val_txt_path,
        "nc": 1,
        "names": ["pill"],
    }
    with open(yaml_path, "w", encoding="utf-8") as file:
        yaml.dump(data_yaml, file, default_flow_style=False, allow_unicode=True)

    logger.info("Dataset ready | train=%d val=%d | yaml=%s", len(train_items), len(val_items), yaml_path)
    return yaml_path


def cleanup_dataset(job_id: str) -> None:
    yaml_dir = f"/tmp/datasets/{job_id}"
    if os.path.exists(yaml_dir):
        shutil.rmtree(yaml_dir)
        logger.info("Cleaned up temp dataset dir for job %s", job_id)
