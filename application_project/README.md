# 📱 application_project

## 개요
  이 프로젝트는 알약 관리 및 판별을 위한 안드로이드 어플리케이션입니다. 
  
### 1. 주요 기능
  1. 약물 복용 시간 알림.
  2. 약물 기록 관리
  3. 약물 판별 및 정보조회 

### 2. 기술 스택
- **프로그래밍 언어**<br>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white">
- **개발 환경**<br>
  <img src="https://img.shields.io/badge/Android Studio-3DDC84?style=for-the-badge&logo=Android Studio&logoColor=white">
- **배포 플랫폼**: Docker, AWS

## 📂 프로젝트 구성
```plaintext
application_project/
|
├── data/ # 데이터 관련 코드 (Model 계층)
│ ├── local/ # 로컬 데이터베이스 (Room 등)
│ │ ├── dao/ # Data Access Object 인터페이스
│ │ └── entities/ # 데이터 엔티티 클래스
│ ├── remote/ # 네트워크 관련 코드 (Retrofit 등)
│ │ ├── api/ # API 인터페이스 정의
│ │ └── models/ # 네트워크 응답 모델
│ └── repository/ # 데이터 소스 관리 (로컬 + 원격 통합)
|
├── domain/ # 비즈니스 로직 계층 (선택적)
│ └── usecases/ # 유스케이스 클래스 (비즈니스 로직 처리)
|
├── ui/ # 사용자 인터페이스 관련 코드 (View 계층)
│ ├── activities/ # 액티비티 클래스
│ ├── fragments/ # 프래그먼트 클래스
│ └── adapters/ # RecyclerView 어댑터 등 UI 관련 어댑터
|
├── viewmodel/ # ViewModel 계층
│ └── MainViewModel.kt # ViewModel 클래스 정의
|
├── utils/ # 유틸리티 클래스 및 헬퍼 함수
│ ├── extensions/ # Kotlin 확장 함수
│ └── constants/ # 상수 값 정의
|
├── di/ # 의존성 주입 설정 (Hilt)
│ └── AppModule.kt # Hilt 모듈 설정 파일
|
├── navigation/ # Jetpack Navigation 구성 파일
│ └── NavGraph.kt # 네비게이션 그래프 정의
|
└── README.md # 어플리케이션 프로젝트 설명
```


## 사용 방법
1. 레포지토리를 클론합니다:
