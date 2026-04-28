import json
import logging
import os
import urllib.error
import urllib.request

logger = logging.getLogger(__name__)

SPRING_CALLBACK_BASE_URL = os.getenv("SPRING_CALLBACK_BASE_URL", "http://alyak-api:8080")
SPRING_CALLBACK_TOKEN = os.getenv("TRAINING_CALLBACK_TOKEN", "local-training-callback-token")


def notify_spring_completion(job_id: str, status: str, progress: int, message: str) -> None:
    callback_url = f"{SPRING_CALLBACK_BASE_URL}/api/internal/training/jobs/{job_id}/complete"
    payload = {"status": status, "progress": progress, "message": message}
    req = urllib.request.Request(
        callback_url,
        data=json.dumps(payload).encode("utf-8"),
        method="PATCH",
        headers={
            "Content-Type": "application/json",
            "X-Internal-Token": SPRING_CALLBACK_TOKEN,
        },
    )

    try:
        with urllib.request.urlopen(req, timeout=5) as response:
            if response.status >= 300:
                logger.warning("Spring callback non-success status: %s", response.status)
    except urllib.error.HTTPError as e:
        logger.warning("Spring callback failed with HTTPError: %s", e)
    except Exception as e:
        logger.warning("Spring callback failed: %s", e)
