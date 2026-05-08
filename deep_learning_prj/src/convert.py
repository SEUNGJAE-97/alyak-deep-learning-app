"""
pill_metadata.json → Unsloth VLM 파인튜닝용 JSONL 변환 스크립트
앞면(front) 이미지만 사용

사용법:
    python convert_to_jsonl.py \
        --input pill_metadata.json \
        --image_dir ./images \
        --output_dir ./dataset \
        --val_split 0.1

출력:
    dataset/train.jsonl
    dataset/val.jsonl
    dataset/stats.txt
"""

import json
import random
import os
import argparse
from collections import Counter


def make_sample(item: dict) -> dict:
    m = item["metadata"]
    answer = f"색상: {m['색상']}\n제형: {m['제형']}\n표시: {m['표시']}"

    return {
        "messages": [
            {
                "role": "user",
                "content": [
                    {"type": "image", "image": item["image_filename"]},
                    {"type": "text",  "text": "이 알약 앞면의 색상, 제형, 표시를 추출하세요."}
                ]
            },
            {
                "role": "assistant",
                "content": answer
            }
        ]
    }


def convert(input_path, image_dir, output_dir, val_split, seed):
    with open(input_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    # front만 필터링
    front_data = [d for d in data if d["part_type"] == "front"]

    # 이미지 파일 존재 확인 (image_dir 지정 시)
    if image_dir:
        front_data = [
            d for d in front_data
            if os.path.exists(os.path.join(image_dir, d["image_filename"]))
        ]

    # 샘플 생성
    samples = [make_sample(d) for d in front_data]

    # 셔플 & 분할
    random.seed(seed)
    random.shuffle(samples)
    val_n = max(1, int(len(samples) * val_split))
    val_samples   = samples[:val_n]
    train_samples = samples[val_n:]

    # 저장
    os.makedirs(output_dir, exist_ok=True)

    def write_jsonl(path, items):
        with open(path, "w", encoding="utf-8") as f:
            for item in items:
                f.write(json.dumps(item, ensure_ascii=False) + "\n")

    train_path = os.path.join(output_dir, "train.jsonl")
    val_path   = os.path.join(output_dir, "val.jsonl")
    write_jsonl(train_path, train_samples)
    write_jsonl(val_path,   val_samples)

    # 통계
    colors = Counter(d["metadata"]["색상"] for d in front_data)
    shapes = Counter(d["metadata"]["제형"] for d in front_data)

    stat_lines = [
        "=== 변환 완료 ===",
        f"front 샘플 수  : {len(front_data)}",
        f"학습 샘플 수   : {len(train_samples)}",
        f"검증 샘플 수   : {len(val_samples)}",
        "",
        "색상 분포:",
        *[f"  {c}: {n}" for c, n in colors.most_common()],
        "",
        "제형 분포:",
        *[f"  {s}: {n}" for s, n in shapes.most_common()],
        "",
        "저장 위치:",
        f"  {train_path}",
        f"  {val_path}",
    ]
    stat_text = "\n".join(stat_lines)
    print(stat_text)

    with open(os.path.join(output_dir, "stats.txt"), "w", encoding="utf-8") as f:
        f.write(stat_text)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--input",      default="pill_metadata.json")
    parser.add_argument("--image_dir",  default="",
                        help="이미지 폴더 경로 (비우면 존재 확인 스킵)")
    parser.add_argument("--output_dir", default="dataset")
    parser.add_argument("--val_split",  type=float, default=0.1)
    parser.add_argument("--seed",       type=int,   default=42)
    args = parser.parse_args()

    convert(args.input, args.image_dir, args.output_dir, args.val_split, args.seed)
