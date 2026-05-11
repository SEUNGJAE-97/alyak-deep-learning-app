# ALYAK

가족 단위 복약 관리와 알약 인식 기능을 중심으로 구성된 모노레포입니다.  
현재 저장소에는 사용자용 Android 앱뿐 아니라 백엔드 API, 관리자용 학습 대시보드, 모델 실험 산출물까지 함께 포함되어 있습니다.

## 프로젝트 개요

ALYAK은 크게 두 흐름으로 나뉩니다.

- 사용자 서비스: 로그인, 가족 초대, 복약 스케줄, 복용 기록, 약 검색, 촬영 기반 알약 인식
- 관리자 운영: 라벨링 데이터 검수, 학습 데이터셋 관리, 파인튜닝 실행, 모델 아카이브 조회 및 비교

## 저장소 구성

| 경로                                                               | 역할                                                        | 주요 스택                                                 |
| ------------------------------------------------------------------ | ----------------------------------------------------------- | --------------------------------------------------------- |
| [application_project](./application_project/README.md)             | 사용자용 Android 앱                                         | Kotlin, Jetpack Compose, Hilt, Room, Retrofit, FCM        |
| [alyak-api-server](./alyak-api-server/README.md)                   | 메인 API 서버, 인증, 가족/복약/약품 도메인, 관리자 API      | Spring Boot 3, Java 17, JPA, MySQL, Redis, Firebase Admin |
| [Pill-Recognition-Pipeline](./Pill-Recognition-Pipeline/README.md) | 알약 이미지 OCR 및 추론용 Python 서비스                     | FastAPI, Ultralytics                                      |
| [admin_front](./admin_front/README.md)                             | 관리자용 웹 대시보드                                        | React, TypeScript, Vite                                   |
| `[deep_learning_prj/](./deep_learning_prj/)`                       | 모델 가중치, 추론 결과, 실험 노트북 등 테스트 과정의 산출물 | PyTorch/YOLO 계열 산출물 포함                             |
| `[shared-images/](./shared-images/)`                               | 서비스 간 공용 이미지 저장 용도 디렉터리                    | 정적 파일 저장                                            |

## 아키텍처

### 사용자 서비스 흐름

```text
[Android 앱]
    │
    │ HTTPS / JWT
    ▼
[Spring Boot API]
    │ 인증, 가족, 복약, 스케줄, 약 검색
    │ POST /api/pill/recognize
    ▼
[FastAPI 판별 서비스]
    │ 이미지 분석 / 판별 결과 반환
    └─ Spring이 후처리 후 앱에 통합 응답
```

- Android 앱은 FastAPI를 직접 호출하지 않고 Spring API를 통해 알약 인식 기능을 사용합니다.
- 복약 로그 저장 시 서버에서 가족 구성원 대상 푸시 알림을 처리할 수 있습니다.
- 스케줄 백업/복구 기능은 Spring API를 통해 동기화됩니다.

### 관리자 학습 운영 흐름

```text
[admin_front]
    │ 관리자 로그인 후 Bearer 토큰 사용
    ▼
[Spring Boot Admin API]
    │ /api/admin/labeling/*
    │ /api/admin/training/*
    │ /api/admin/archives/*
    ▼
[FastAPI / 내부 학습 서비스]
    │ 데이터셋 조회, 학습 실행, 상태 스트리밍
    ▼
[모델 아카이브 저장 및 비교]
```

- `admin_front`는 라벨링 이미지 검수, 바운딩 박스 수정, 상태 변경, 학습 시작, 모델 비교 화면을 제공합니다.
- 관리자 API는 `hasRole('ADMIN')` 권한이 필요한 컨트롤러로 분리되어 있습니다.
- 학습 완료 후 모델 아카이브를 갱신해 버전별 성능 비교에 활용합니다.

## 주요 서브프로젝트

### 1. Android 앱

- 경로: `application_project`
- 목적: 사용자 복약 관리 앱
- 주요 기능:
  - 로그인/회원가입
  - 가족 초대 및 QR 기반 연동
  - 복약 스케줄 및 복용 기록
  - 약 검색 및 상세 조회
  - 카메라 촬영 기반 알약 인식
  - FCM 푸시 및 인앱 알림

자세한 빌드/실행 정보는 [`application_project/README.md`](./application_project/README.md)를 참고하세요.

### 2. Spring Boot API 서버

- 경로: `alyak-api-server`
- 목적: 사용자 API와 관리자 API의 중심 백엔드
- 대표 도메인:
  - 인증: `/api/auth/*`
  - 사용자: `/api/users/*`
  - 가족: `/api/family/*`
  - 약품: `/api/pill/*`
  - 복약: `/api/medication/*`
  - 스케줄: `/api/schedule/*`
  - 관리자: `/api/admin/*`

관리자 영역에는 다음 기능이 포함됩니다.

- 라벨링 항목 조회/수정/승인/반려
- 학습 작업 생성 및 상태 조회
- 모델 아카이브 목록/상세/비교

자세한 API, Docker, 환경 변수는 [`alyak-api-server/README.md`](./alyak-api-server/README.md)를 참고하세요.

### 3. 추론 서비스

- 경로: `Pill-Recognition-Pipeline`
- 목적: 알약 이미지 처리와 OCR 기반 보조 추론

예시 실행:

```bash
cd Pill-Recognition-Pipeline
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

자세한 실행 방법과 환경 변수는 [`Pill-Recognition-Pipeline/README.md`](./Pill-Recognition-Pipeline/README.md)를 참고하세요.

### 4. 관리자 웹 대시보드

- 경로: `admin_front`
- 목적: 데이터 라벨링, 학습 실행, 아카이브 비교를 위한 관리자 UI
- 현재 확인된 화면:
  - `Dashboard`: 라벨링 데이터 검수 및 박스 편집
  - `Training`: 하이퍼파라미터 설정 및 학습 작업 시작
  - `TrainingLogs`: 학습 로그 스트림 확인
  - `Archives`: 모델 버전 및 성능 비교

기본 개발 서버:

```bash
cd admin_front
npm install
npm run dev
```

기본적으로 `VITE_API_BASE_URL` 환경 변수를 사용하며, 값이 없으면 `http://localhost:8080`을 바라봅니다.

### 5. 딥러닝 실험 산출물

- 경로: `deep_learning_prj`
- 목적: 모델 파일, 테스트 이미지, 추론 결과, 노트북 등 실험 산출물 보관
- 현재 `.pt`, `.onnx`, `.tflite`, `ipynb`, 예측 결과 이미지가 포함되어 있습니다.

이 디렉터리는 모델 학습 및 데이터 가공을 위해 사용한 산출물 입니다.

## 실행 전 설정 가이드

단순 `docker compose up` 또는 `npm run dev`만으로 바로 실행이 불가능합니다.  
서브프로젝트별로 환경 변수, 비밀 키, 외부 서비스 설정 파일이 필요하므로 먼저 아래 항목을 준비하는 것을 권장합니다.

### 백엔드 (`alyak-api-server`) 준비 사항

`alyak-api-server/docker-compose.yml` 기준으로 다음 준비물이 필요합니다.

#### 1. `.env` 파일

`alyak-api-server/.env` 파일이 필요합니다.  
`docker-compose.yml`에서 `env_file: .env`를 사용하고 있으므로, 운영/개발용 비밀값은 여기에 분리하는 편이 안전합니다.

대표적으로 확인할 항목:

- 데이터베이스 접속 정보
- Redis 접속 정보
- 메일 발송 계정 정보
- JWT 시크릿
- Firebase 관련 설정
- Fast API 서버 URL
- 내부 학습 콜백 토큰

실제 설정 키는 `alyak-api-server/src/main/resources/application.yml`과 `alyak-api-server/docker-compose.yml`을 함께 보면 확인할 수 있습니다.

주요 키 예시:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- `JWT_SECRET`
- `OCR_SERVER_URL`
- `TRAINING_CALLBACK_TOKEN`
- `FIREBASE_CREDENTIALS_PATH`
- `FIREBASE_PROJECT_ID`
- `APP_UPLOAD_ROOT_PATH`
- `ARCHIVE_RUNS_ROOT`

#### 2. Firebase 서비스 계정 JSON

Spring 백엔드는 `prod` 프로필에서 Firebase 초기화 시 서비스 계정 JSON 파일을 필수로 요구합니다.

- 설정 키: `FIREBASE_CREDENTIALS_PATH`
- Docker Compose 기본 마운트 경로: `/run/secrets/firebase-adminsdk.json`

Firebase 설정은 `alyak-api-server/src/main/java/.../FirebaseConfig.java`에서 강제됩니다.

#### 3. 업로드/아카이브 공유 디렉터리

백엔드와 학습 서비스는 `shared-images` 디렉터리를 공유해서 사용합니다.

- 업로드 이미지
- 라벨 파일
- 학습 결과물 및 모델 아카이브

관련 설정:

- `APP_UPLOAD_ROOT_PATH`
- `ARCHIVE_RUNS_ROOT`

Docker Compose 기준으로 `../shared-images`가 각 컨테이너에 마운트됩니다.

#### 4. 내부 학습 토큰 정합성

관리자 학습 기능을 사용하려면 Spring과 Python 서비스가 같은 내부 토큰을 공유해야 합니다.

- Spring: `TRAINING_CALLBACK_TOKEN`
- Python 서비스 내부 요청 헤더: `X-Internal-Token`

이 값이 다르면 다음 기능이 동작하지 않을 수 있습니다.

- TRAINING_SET 데이터 조회
- 학습 완료 콜백
- 모델 아카이브 반영

### VLM / Fast API (`Pill-Recognition-Pipeline`) 준비 사항

Fast API는 단독 알약 특징 탐지 서버이기도 하고, 관리자 학습 파이프라인의 내부 서비스 역할도 합니다.

확인할 설정:

- `SPRING_BASE_URL`: 내부 라벨링 데이터 조회용 Spring 주소
- `SPRING_CALLBACK_BASE_URL`: 학습 완료 콜백을 보낼 Spring 주소
- `TRAINING_CALLBACK_TOKEN`: Spring의 내부 토큰과 동일해야 함
- `SHARED_IMAGE_BASE`: 업로드 이미지 실제 저장 경로
- `SHARED_LABEL_BASE`: 생성 라벨 저장 경로
- `TRAINING_RUNS_ROOT`: 학습 산출물 저장 경로

특히 Docker Compose를 사용할 때와 로컬에서 직접 띄울 때 기본 URL이 달라질 수 있으니 주의해야 합니다.

### Android 앱 (`application_project`) 준비 사항

Android 앱은 코드만으로 바로 실행되지 않고 Firebase 및 로컬 개발 설정이 필요합니다.

#### 1. `google-services.json`

Firebase 프로젝트에서 발급받은 Android용 `google-services.json` 파일이 필요합니다.

일반적으로 다음 위치에 둡니다.

- `application_project/app/google-services.json`

이 파일은 Git에 포함하지 않는 것이 일반적이며, 현재 `.gitignore`에도 제외 대상으로 잡혀 있습니다.

#### 2. `local.properties` 또는 키 관리 설정

다음 값들은 로컬 설정 또는 별도 secrets 관리 방식으로 주입해야 합니다.

- Android SDK 경로
- 카카오 관련 키
- 지도 API 관련 키
- 필요 시 서버 베이스 URL 또는 BuildConfig 값

프로젝트 README에도 `google-services.json`, Kakao/지도 키, `local.properties` 계열 설정이 필요하다고 명시되어 있습니다.

### 관리자 웹 (`admin_front`) 준비 사항

`admin_front`는 기본적으로 Spring 관리자 API를 바라봅니다.

확인할 파일:

- `admin_front/.env.example`

현재 코드 기준으로 실제로 중요한 값은 다음입니다.

- `VITE_API_BASE_URL`: Spring 서버 주소
- `VITE_FAST_API_BASE_URL`: FastAPI 학습 로그 스트림 서버 주소

예:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_FAST_API_BASE_URL=http://localhost:8001
```

### 어떤 파일을 직접 준비해야 하나

실행 전 보통 아래 파일들을 로컬에 준비하게 됩니다.

- `alyak-api-server/.env`
- `alyak-api-server/firebase-adminsdk.json`
- `application_project/app/google-services.json`
- `application_project/local.properties`
- `admin_front/.env.example`

### 추천 문서 확인 순서

실제로 환경을 맞출 때는 아래 순서로 보면 편합니다.

1. 루트 `README.md`에서 전체 구조 확인
2. `alyak-api-server/README.md`와 `docker-compose.yml`에서 백엔드 설정 확인
3. `application_project/README.md`에서 Android 필수 파일 확인
4. `admin_front/.env.example`와 실제 코드의 `VITE_API_BASE_URL`, `VITE_FAST_API_BASE_URL` 사용 위치 확인
5. Python 서비스 코드에서 Spring 연동용 환경 변수 확인

## 문서 링크

- [루트 개요](./README.md)
- [Android 앱 문서](./application_project/README.md)
- [Spring Boot API 문서](./alyak-api-server/README.md)
- [관리자 웹 문서](./admin_front/README.md)
- [OCR / Python 서비스 문서](./Pill-Recognition-Pipeline/README.md)
