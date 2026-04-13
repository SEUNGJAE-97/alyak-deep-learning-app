# application_project (Android)

가족 단위 **복약 관리**, **약 검색·상세**, **촬영·QR 기반 인식**, **지도(약국 등)** 를 제공하는 **Kotlin / Jetpack Compose** 기반 안드로이드 앱 모듈입니다.  
백엔드는 모노레포의 [`alyak-api-server`](../alyak-api-server/) **Spring Boot API**와 통신하며, 알약 OCR은 앱이 FastAPI를 직접 호출하지 않고 **Spring의 `/api/pill/recognize`** 를 통해 처리합니다.

## 주요 기능

- 인증·회원 (로그인, 회원가입, 비밀번호 찾기 등)
- 가족 구성·초대·QR 연동
- 메인 대시보드·복약 스케줄·복용 이력
- 약 검색·상세, 최근 검색(Room)
- CameraX 촬영·알약 인식(Spring 경유), QR 스캔
- 복약 로그·통계, 로컬 알람·**FCM** 푸시·인앱 알림 배너
- 스케줄 백업/복구 API 연동
- 카카오 지도·로컬 API 기반 지도 화면

## 기술 스택

| 구분 | 내용 |
|------|------|
| 언어 | Kotlin (`gradle/libs.versions.toml` 기준 2.2.x) |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| DI | Hilt |
| 네트워크 | Retrofit 2, OkHttp, Gson |
| 로컬 DB | Room |
| 기타 저장소 | DataStore Preferences |
| 푸시 | Firebase Cloud Messaging |
| 지도 | Kakao Map SDK, Kakao 로컬 API |
| 이미지 | Coil 2/3 |
| 기타 | CameraX, TensorFlow Lite, ZXing, Lottie, Chrome Custom Tabs |

빌드 도구: **Android Gradle Plugin** 9.x, **Gradle** 9.3.x(Wrapper), JVM 11 타겟.

## Android 버전

| 항목 | 값 |
|------|-----|
| `applicationId` | `com.alyak.detector` |
| `minSdk` | 23 |
| `targetSdk` | 34 |
| `compileSdk` | 35 |

## 백엔드·API와의 관계

- REST 클라이언트는 **`@AppServerRetrofit`** 으로 주입되는 Retrofit 인스턴스를 사용합니다. 구현은 `app/src/main/java/com/alyak/detector/data/api/NetworkModule.kt` 에 있습니다.
- 서버 측 스택은 **Spring Boot 3.x, Spring Data JPA, MySQL** 등이며, API 개요·Swagger·Docker 실행 방법은 [**alyak-api-server/README.md**](../alyak-api-server/README.md) 를 참고하세요.
- **개발 시** API 베이스 URL은 `NetworkModule` 의 `SERVER_URL` 을 환경에 맞게 조정합니다(에뮬레이터에서 호스트 PC는 `10.0.2.2` 등). 저장소에 올라간 값은 로컬 개발용일 수 있으므로 배포·팀 규칙에 맞게 관리하세요.
- `google-services.json` 및 카카오·지도 키 등은 Firebase / Kakao 콘솔에서 발급한 값을 **`secrets` 플러그인·`local.properties`·BuildConfig** 등 프로젝트 설정에 맞춰 넣어야 합니다.

## 소스 구조 (요약)

패키지 루트: `com.alyak.detector`. 기능 단위로 **`feature/`** 가 나뉘어 있습니다.

```text
app/src/main/java/com/alyak/detector/
├── feature/
│   ├── auth/          # 로그인·회원가입
│   ├── family/        # 가족·초대
│   ├── camera/        # 촬영·OCR API·QR
│   ├── pill/          # 약 검색·상세·Room(최근 검색)
│   ├── notification/  # 복약 로그·스케줄 API·알람·로컬 백업 엔티티
│   ├── map/           # 지도·카카오 API
│   ├── user/          # 사용자 설정
│   └── splash/        # 스플래시
├── push/              # FCM 서비스·알림 DAO·수신 처리
├── navigation/        # NavGraph
├── data/api/          # Retrofit 모듈(NetworkModule 등)
├── core/network/      # 인증 인터셉터·토큰 갱신
├── di/                # Hilt 모듈
└── ui/                # 공통 테마·컴포넌트
```

## 실행 방법

1. 상위 저장소를 클론한 뒤 Android Studio에서 **`application_project`** 디렉터리를 엽니다.
2. 필요한 경우 `alyak-api-server` 를 기동하고, `NetworkModule` 의 서버 URL을 맞춥니다.
3. Firebase·Kakao 등 키 설정을 완료합니다.
4. Gradle 동기화 후 **`app`** 구성으로 실행합니다.

명령줄 예시:

```bash
cd application_project
./gradlew :app:assembleDebug
```

## 관련 문서

- [모노레포 개요](../README.md)
- [Spring Boot API](../alyak-api-server/README.md)
