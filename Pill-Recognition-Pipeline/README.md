# Pill-Recognition-Pipeline

ALYAK의 Python 기반 OCR 및 학습 서비스입니다.  
사용자용 알약 인식 요청을 처리하는 FastAPI 서버이면서, 관리자 학습 파이프라인에서는 데이터셋 구성, 학습 실행, 로그 스트리밍, Spring 콜백까지 담당합니다.

## 주요 역할

- 이미지 업로드 기반 VLM(qwen2.5 3b)모델이 텍스트, 외형, 색상 추출
- 관리자 학습 작업 생성
- 학습 상태 조회
- 학습 로그 SSE 스트리밍
- Spring Boot 내부 API에서 TRAINING_SET 데이터 조회
- 학습 완료 후 Spring Boot로 콜백 전송

## 기술 스택

| 구분 | 내용 |
|------|------|
| 언어 | Python 3.11 |
| 웹 프레임워크 | FastAPI |
| 서버 | Uvicorn |
| 모델 | Qwen2.5 3b |
| 학습 | Ultralytics |
| HTTP 클라이언트 | httpx |

## API 개요

### OCR API

- `POST /api/v1/process`
- 멀티파트 이미지 업로드를 받아 OCR 결과를 반환합니다.

### 학습 API

- `POST /train/jobs` - 학습 작업 생성
- `GET /train/jobs/{job_id}` - 학습 작업 상태 조회
- `GET /train/system/status` - 학습 서버 상태 조회
- `GET /train/jobs/{job_id}/logs/stream` - 학습 로그 SSE 스트리밍

## Spring Boot와의 관계

이 서비스는 Spring Boot와 두 가지 방식으로 연결됩니다.

### 1. 사용자 알약 인식 흐름

Android 앱은 이 서비스를 직접 호출하지 않습니다.  
앱은 Spring Boot의 알약 인식 API를 호출하고, Spring이 내부에서 OCR 요청을 위임한 뒤 결과를 후처리해서 반환합니다.

### 2. 관리자 학습 흐름

- Python 서비스가 Spring 내부 API에서 라벨링 데이터를 조회합니다.
- 학습 완료 후 Spring 내부 콜백 API로 결과를 전달합니다.
- Spring은 이를 바탕으로 학습 이력과 모델 아카이브를 갱신합니다.

관련 내부 연동 경로:

- `GET /api/internal/labeling/items`
- `GET /api/internal/labeling/items/{id}`
- `PATCH /api/internal/training/jobs/{externalJobId}/complete`

## 환경 변수

주요 환경 변수는 다음과 같습니다.

| 변수 | 설명 |
|------|------|
| `SPRING_BASE_URL` | Spring 내부 라벨링 API 주소 |
| `SPRING_CALLBACK_BASE_URL` | 학습 완료 콜백을 보낼 Spring 주소 |
| `TRAINING_CALLBACK_TOKEN` | Spring 내부 토큰과 일치해야 하는 값 |
| `SHARED_IMAGE_BASE` | 업로드 이미지 실제 저장 경로 |
| `SHARED_LABEL_BASE` | 생성 라벨 저장 경로 |
| `TRAINING_RUNS_ROOT` | 학습 결과물 저장 경로 |

기본값은 Docker Compose 기준 주소를 전제로 하고 있으므로, 로컬 직접 실행 시에는 환경에 맞게 조정해야 합니다.

## 실행 방법

### 로컬 실행

```bash
cd Pill-Recognition-Pipeline
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

기본 포트는 `8000`입니다.

### Docker 실행

`Dockerfile`은 Python 3.11 slim 이미지를 사용하며, OCR 처리에 필요한 시스템 패키지와 Python 의존성을 함께 설치합니다.

```bash
cd Pill-Recognition-Pipeline
docker build -t pill-recognition-pipeline .
docker run -p 8000:8000 pill-recognition-pipeline
```

실제 프로젝트에서는 보통 단독 실행보다 `alyak-api-server/docker-compose.yml`을 통해 함께 기동합니다.

## 디렉터리 구성

```text
Pill-Recognition-Pipeline/
├── app/main.py
├── app/api/endpoints/ocr_router.py
├── app/api/endpoints/training_router.py
├── app/services/ocr_service.py
├── app/services/train_service.py
├── app/services/dataset_builder.py
├── app/utils/callback.py
├── requirements.txt
└── Dockerfile
```

## 참고 사항

- CORS는 기본적으로 `localhost:3000`, `localhost:5173` 등을 허용합니다.
- 학습 로그 스트림은 관리자 웹에서 직접 소비합니다.
- 학습 데이터셋 생성은 공유 디렉터리와 Spring 내부 API 접근이 모두 가능해야 정상 동작합니다.

## 관련 문서

- [모노레포 개요](../README.md)
- [Spring Boot API](../alyak-api-server/README.md)
- [관리자 웹](../admin_front/README.md)
