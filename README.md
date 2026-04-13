# ALYAK (alyak-deep-learning-app)

가족 단위 **복약 관리**와 **알약 정보 조회·촬영 인식**을 제공하는 모노레포입니다.  
클라이언트는 **Android**, 백엔드는 **Spring Boot**, 알약 이미지 **OCR**은 **FastAPI** 마이크로서비스로 분리되어 있습니다.

## 레포지토리 구성

| 디렉터리 | 역할 | 스택 |
|----------|------|------|
| [`application_project/`](./application_project/) | Android 앱 | Kotlin, Jetpack Compose, Hilt, Room, Retrofit, FCM |
| [`alyak-api-server/`](./alyak-api-server/) | REST API · 비즈니스 로직 · DB · 푸시 연동 | Spring Boot 3.x, Spring Data JPA, MySQL, Firebase Admin(FCM), Docker |
| [`Pill-Recognition-Pipeline/`](./Pill-Recognition-Pipeline/) | 알약 포장 문자 OCR API | FastAPI, EasyOCR, Uvicorn, Docker |

### 서비스 간 데이터 흐름 (요약)

```
[Android 앱]
    │  HTTPS (JWT 등)
    ▼
[Spring Boot : alyak-api-server]
    │  인증, 가족, 복약 로그, 스케줄 백업, 약 검색…
    │  `/api/pill/recognize` 호출 시
    ▼
[FastAPI : Pill-Recognition-Pipeline]
    `/api/v1/process` — 이미지 OCR → 텍스트
    ▲
    └─ Spring이 OCR 결과로 DB 조회 후 앱에 통합 응답 (앱은 Spring만 호출)
```

- **앱**은 FastAPI 주소를 직접 쓰지 않고, **`POST /api/pill/recognize`**(Spring) 한 번으로 촬영 인식·상세 조회를 처리합니다.
- **복약 기록**은 **`POST /api/medication/log`** 로 저장되며, 서버에서 같은 가족 구성원 기기로 **FCM 데이터 메시지**를 보낼 수 있습니다.
- **스케줄 백업**은 **`/api/schedule/backup`**, 재설치 복구는 **`/api/schedule/restore`** 등으로 동기화합니다.

## Spring Boot API 도메인 (요약)

| 영역 | 경로 예시 | 설명 |
|------|-----------|------|
| 인증 | `/api/auth/*`, OAuth 콜백 | 로그인·회원가입·토큰 재발급 |
| 사용자 | `/api/users/me` | 내 정보 |
| 가족 | `/api/family/*` | 구성원·초대·QR |
| 약품 | `/api/pill/*` | 검색·상세·**이미지 인식(내부 OCR 연동)** |
| 복약 | `/api/medication/*` | 복용 로그·통계 |
| 스케줄 | `/api/schedule/*` | 백업·복구·가족 스케줄 조회 |
| 알림 토큰 | `/api/notifications/*` | FCM 디바이스 등록 |
| 기타 | `/api/map`, 이메일 인증 등 | |

상세 실행 방법·Swagger·Docker는 [**alyak-api-server/README.md**](./alyak-api-server/README.md)를 참고하세요.

## FastAPI (OCR)

- 엔트리: `Pill-Recognition-Pipeline/app/main.py`
- 라우터: `POST /api/v1/process` — 멀티파트 이미지 업로드 후 OCR 결과 JSON 반환
- 로컬 실행 예: `uvicorn app.main:app --host 0.0.0.0 --port 8000` (패키지 루트·가상환경 기준은 해당 폴더 README 참고)

## Android 앱

- 모듈: `application_project/app`
- 최소 SDK / 타겟 등은 [**application_project/README.md**](./application_project/README.md) 참고
- 서버 베이스 URL·빌드 설정은 `BuildConfig` / Gradle 설정에 맞춥니다.

## 사용 방법

1. 저장소를 클론합니다.
   ```bash
   git clone https://github.com/SEUNGJAE-97/alyak-deep-learning-app.git
   cd alyak-deep-learning-app
   ```
2. **백엔드**: `alyak-api-server`에서 Docker Compose 또는 Gradle로 기동 (MySQL·환경 변수 필요 시 `README` 참고).
3. **OCR 서비스**: `Pill-Recognition-Pipeline`에서 FastAPI 기동 — Spring의 OCR 연동 URL이 해당 서비스를 가리키도록 설정합니다.
4. **앱**: Android Studio에서 `application_project`를 열고 실행합니다.

## 문서 링크

- [Android (`application_project/README.md`)](./application_project/README.md)
- [Spring Boot (`alyak-api-server/README.md`)](./alyak-api-server/README.md)

---

*과거 문서에 있던 `classifier_project`(Keras 분류기) 경로는 현재 본 저장소 트리에 포함되어 있지 않습니다. 딥러닝 실험 코드는 별도 브랜치·저장소로 관리할 수 있습니다.*
