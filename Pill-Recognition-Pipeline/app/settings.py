import os
from pathlib import Path

from dotenv import load_dotenv

_PIPELINE_ROOT = Path(__file__).resolve().parent.parent
_REPO_ROOT = _PIPELINE_ROOT.parent

load_dotenv(_PIPELINE_ROOT / ".env")

SPRING_BASE_URL = os.getenv("SPRING_BASE_URL", "http://localhost:8080")
SPRING_CALLBACK_BASE_URL = os.getenv("SPRING_CALLBACK_BASE_URL", "http://localhost:8080")
TRAINING_CALLBACK_TOKEN = os.getenv("TRAINING_CALLBACK_TOKEN", "local-training-callback-token")

SHARED_IMAGE_BASE = os.getenv(
    "SHARED_IMAGE_BASE",
    str(_REPO_ROOT / "shared-images" / "uploads"),
)
SHARED_LABEL_BASE = os.getenv(
    "SHARED_LABEL_BASE",
    str(_REPO_ROOT / "shared-images" / "labels"),
)
TRAINING_RUNS_ROOT = os.getenv(
    "TRAINING_RUNS_ROOT",
    str(_REPO_ROOT / "shared-images" / "model-runs"),
)
BASE_MODEL_PATH = os.getenv("BASE_MODEL_PATH", "models/pill_trained.pt")
