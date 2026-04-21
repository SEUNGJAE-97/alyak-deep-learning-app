# Alyak API Server

복약·가족·약품·알림을 다루는 **Spring Boot** 백엔드입니다.  
클라이언트(Android)와 **REST API**로 통신하고, 알약 촬영 인식 시 **FastAPI(OCR)** 서버를 호출합니다. **FCM**으로 가족 초대·복약 알림 푸시를 보냅니다(`prod` 프로필).

## 기술 스택

| 구분 | 내용 |
|------|------|
| 런타임 | **Java 17** |
| 프레임워크 | **Spring Boot 3.5.x** |
| 빌드 | **Gradle** (`./gradlew`) |
| 데이터 | **Spring Data JPA**, **MySQL 8** |
| 기타 | Spring Security, Redis, Mail, Firebase Admin(FCM), SpringDoc OpenAPI |

## 패키지 구조 (도메인)

```
src/main/java/com/github/seungjae97/alyak/alyakapiserver/
├── domain/auth/          # 로그인·회원가입·OAuth 콜백·토큰
├── domain/user/          # 내 정보·비밀번호·탈퇴
├── domain/family/        # 가족 구성원·초대·QR
├── domain/pill/          # 약 검색·상세·이미지 인식(OCR 연동)
├── domain/medication/    # 복용 로그·통계
├── domain/schedule/      # 스케줄 백업·복구·삭제
├── domain/notification/  # FCM 디바이스 토큰 등록
├── domain/map/           # 지도/약국 등
├── global/               # Redis·이메일 인증 등 공통
└── AlyakApiServerApplication.java
```

## 주요 API (요약)

인증이 필요한 엔드포인트는 Spring Security 설정에 따릅니다.  
**API 문서(Swagger UI):** 서버 기동 후 `/swagger-ui.html` (설정: `springdoc.swagger-ui.path`)

### 인증 (`/api/auth`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/password/reset` | 비밀번호 재설정 |
| POST | `/api/auth/logout` | 로그아웃 |
| POST | `/api/auth/reissue` | 토큰 재발급 |
| POST | `/api/auth/temp-login` | 임시 로그인 |

### OAuth

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/auth/kakao/authorize` | 카카오 |
| GET | `/auth/kakao/callback` | 카카오 콜백 |
| POST | `/auth/google/authorize` | 구글 |
| GET | `/auth/google/callback` | 구글 콜백 |

### 사용자 (`/api/users`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/users/me` | 내 정보 |
| PUT | `/api/users/password` | 비밀번호 변경 |
| DELETE | `/api/users` | 회원 탈퇴 |

### 가족 (`/api/family`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/family/members` | 가족 구성원·통계 등 |
| GET | `/api/family/qrcode` | QR 코드 |
| POST | `/api/family/invite` | 이메일 초대 |
| POST | `/api/family/invite/accept` | 초대 수락 |
| POST | `/api/family/join/qr` | QR로 가족 참여 |

### 약품 (`/api/pill`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/pill/find` | 이름으로 조회 |
| GET | `/api/pill/search` | 속성 검색 |
| GET | `/api/pill/detail` | 상세 |
| POST | `/api/pill/recognize` | 이미지 인식(FastAPI OCR 연동 후 DB 매칭) |
| GET | `/api/pill/autocomplete` | 자동완성 |

### 복약 (`/api/medication`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/medication/log` | 복용 로그 저장(서버에서 상태 판정, 가족 FCM 가능) |
| GET | `/api/medication/stats` | 본인 통계 |
| GET | `/api/medication/stats/{userId}` | 가족 통계(권한 검증) |
| GET | `/api/medication/weekly/{userId}` | 주간 통계 |

### 스케줄 백업 (`/api/schedule`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/schedule/searchFamily` | 가족 스케줄 백업 목록 |
| POST | `/api/schedule/backup` | 백업 저장 |
| GET | `/api/schedule/restore` | 본인 백업 목록(재설치 복구용) |
| DELETE | `/api/schedule/{scheduleId}` | 백업 한 건 삭제 |

### 알림 토큰 (`/api/notifications`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| PUT | `/api/notifications/device-token` | FCM 토큰 등록/갱신 |
| DELETE | `/api/notifications/device-token` | 토큰 삭제 |

### 지도 (`/api/map`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/map` | 지도 관련 API(쿼리는 구현 참고) |

### 이메일 (`/api/email`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/email/send` | 발송 |
| POST | `/api/email/send/reset` | 재설정 메일 |
| POST | `/api/email/verify` | 인증 |

## Docker Compose로 실행

저장소 루트가 `alyak-api-server`일 때, `docker-compose.yml`에 다음이 정의되어 있습니다.

- **mysql** — 호스트 `3307` → 컨테이너 `3306`
- **redis**
- **alyak-api** — Spring Boot `:8080`, `SPRING_PROFILES_ACTIVE=prod` 등 (**`.env`·Firebase 키 경로 필요**)
- **fast-api** — `Pill-Recognition-Pipeline` 빌드, 호스트 `8001` → 컨테이너 `8000` (`OCR_SERVER_URL`은 컨테이너 네트워크 기준)
- **valhalla** — 라우팅(지도 데이터 빌드에 시간 소요)

```bash
cd alyak-api-server

# .env 및 Firebase 서비스 계정 JSON 경로를 docker-compose에 맞게 준비한 뒤
docker compose up -d

docker compose logs -f alyak-api
```

중지·볼륨 삭제:

```bash
docker compose down
docker compose down -v   # DB 등 볼륨까지 삭제
```

재빌드:

```bash
docker compose up --build -d
```

## 로컬 개발 (Gradle)

1. **MySQL** — Docker 또는 로컬에 DB 생성 후 `application.yml` / 환경 변수에 URL·계정 설정.
2. **Redis** — 필요 시 기동 후 호스트·포트 설정.
3. **OCR** — FastAPI를 띄우고, `OCR_SERVER_URL`(예: `http://localhost:8001`)을 Spring에 맞춤.

```bash
cd alyak-api-server
./gradlew bootRun
```

기본 포트는 보통 **8080** (`SERVER_PORT`로 변경 가능).

## 환경 변수 (예시)

| 변수 | 설명 |
|------|------|
| `SPRING_DATASOURCE_URL` | JDBC URL |
| `SPRING_DATASOURCE_USERNAME` / `PASSWORD` | DB 계정 |
| `SPRING_PROFILES_ACTIVE` | `prod` 등 |
| `OCR_SERVER_URL` | FastAPI OCR 베이스 URL |
| `REDIS_HOST` / `REDIS_PORT` | Redis |
| JWT 관련 | `JWT_SECRET`, 만료 등(설정 파일 참고) |
| Firebase | `FIREBASE_CREDENTIALS_PATH`, FCM용 |

실제 배포용 비밀값은 **저장소에 커밋하지 말고** `.env`·시크릿 매니저로 관리하세요.

## 데이터베이스 (Docker Compose 기본 예시)

Compose 파일 기준 예:

- DB 이름: `alyak`
- 사용자: `alyak_user` (비밀번호는 compose/`init.sql` 참고)
- 호스트에서 접속: `localhost:3307` (매핑된 포트)

---

상위 모노레포 개요는 [**루트 README.md**](../README.md)를 참고하세요.
