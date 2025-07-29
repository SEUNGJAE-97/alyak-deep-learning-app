# Alyak API Server

복약 관리 시스템을 위한 Spring Boot API 서버입니다.

## 기술 스택

- **Spring Boot 3.5.3** (Java 17)
- **Spring Data JPA**
- **MySQL 8.0**
- **Docker & Docker Compose**

## 프로젝트 구조

```
src/main/java/com/github/seungjae97/alyak/alyakapiserver/
├── user/                    # 사용자 관리 도메인
│   ├── controller/         # 사용자 관련 REST API
│   ├── service/           # 사용자 비즈니스 로직
│   ├── repository/        # 사용자 데이터 접근
│   └── entity/           # 사용자 엔티티
├── family/                 # 가족 관리 도메인
│   ├── controller/        # 가족 관련 REST API
│   ├── service/          # 가족 비즈니스 로직
│   ├── repository/       # 가족 데이터 접근
│   └── entity/          # 가족 엔티티
├── pill/                   # 약품 정보 도메인
│   ├── controller/       # 약품 관련 REST API
│   ├── service/         # 약품 비즈니스 로직
│   ├── repository/      # 약품 데이터 접근
│   └── entity/         # 약품 엔티티
├── medication/             # 복용 관리 도메인
│   ├── controller/      # 복용 관련 REST API
│   ├── service/        # 복용 비즈니스 로직
│   ├── repository/     # 복용 데이터 접근
│   └── entity/        # 복용 엔티티
└── AlyakApiServerApplication.java
```

## 도메인별 기능

### 1. 사용자 관리 (User Domain)
- 사용자 등록, 조회, 수정, 삭제
- 이메일 기반 사용자 검색
- 사용자 인증 정보 관리

### 2. 가족 관리 (Family Domain)
- 가족 그룹 생성 및 관리
- 가족 구성원 등록 및 관계 설정
- 가족별 구성원 조회

### 3. 약품 정보 (Pill Domain)
- 약품 정보 등록 및 관리
- 약품 형태별 분류
- 약품 제조사 정보 관리

### 4. 복용 관리 (Medication Domain)
- 사용자별 복용 약품 등록
- 복용 스케줄 관리
- 복용 이력 추적

## Docker로 실행하기

### 1. 사전 요구사항
- Docker
- Docker Compose

### 2. 애플리케이션 실행
```bash
# 전체 서비스 실행 (MySQL + Spring Boot)
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 특정 서비스 로그 확인
docker-compose logs -f alyak-api
```

### 3. 애플리케이션 중지
```bash
# 서비스 중지
docker-compose down

# 볼륨까지 삭제 (데이터 초기화)
docker-compose down -v
```

### 4. 재빌드
```bash
# 코드 변경 후 재빌드
docker-compose up --build -d
```

## API 엔드포인트

### 사용자 관리
- `GET /api/users` - 모든 사용자 조회
- `GET /api/users/{id}` - ID로 사용자 조회
- `GET /api/users/email/{email}` - 이메일로 사용자 조회
- `POST /api/users` - 사용자 생성
- `PUT /api/users/{id}` - 사용자 수정
- `DELETE /api/users/{id}` - 사용자 삭제

### 가족 관리
- `GET /api/families` - 모든 가족 조회
- `GET /api/families/{id}` - ID로 가족 조회
- `POST /api/families` - 가족 생성
- `PUT /api/families/{id}` - 가족 수정
- `DELETE /api/families/{id}` - 가족 삭제

### 가족 구성원 관리
- `GET /api/family-members` - 모든 가족 구성원 조회
- `GET /api/family-members/{id}` - ID로 가족 구성원 조회
- `GET /api/family-members/family/{familyId}` - 가족별 구성원 조회
- `POST /api/family-members` - 가족 구성원 생성
- `PUT /api/family-members/{id}` - 가족 구성원 수정
- `DELETE /api/family-members/{id}` - 가족 구성원 삭제

### 약품 정보 관리
- `GET /api/pills` - 모든 약품 조회
- `GET /api/pills/{id}` - ID로 약품 조회
- `GET /api/pills/shape/{pillShapeId}` - 형태별 약품 조회
- `POST /api/pills` - 약품 생성
- `PUT /api/pills/{id}` - 약품 수정
- `DELETE /api/pills/{id}` - 약품 삭제

### 약품 형태 관리
- `GET /api/pill-shapes` - 모든 약품 형태 조회
- `GET /api/pill-shapes/{id}` - ID로 약품 형태 조회
- `POST /api/pill-shapes` - 약품 형태 생성
- `PUT /api/pill-shapes/{id}` - 약품 형태 수정
- `DELETE /api/pill-shapes/{id}` - 약품 형태 삭제

### 사용자 복용 관리
- `GET /api/user-medications` - 모든 사용자 복용 조회
- `GET /api/user-medications/{id}` - ID로 사용자 복용 조회
- `GET /api/user-medications/user/{userId}` - 사용자별 복용 조회
- `POST /api/user-medications` - 사용자 복용 생성
- `PUT /api/user-medications/{id}` - 사용자 복용 수정
- `DELETE /api/user-medications/{id}` - 사용자 복용 삭제

### 복용 스케줄 관리
- `GET /api/medication-schedules` - 모든 복용 스케줄 조회
- `GET /api/medication-schedules/{id}` - ID로 복용 스케줄 조회
- `GET /api/medication-schedules/user-medication/{userMedicationId}` - 사용자 복용별 스케줄 조회
- `GET /api/medication-schedules/schedule?start={start}&end={end}` - 기간별 스케줄 조회
- `POST /api/medication-schedules` - 복용 스케줄 생성
- `PUT /api/medication-schedules/{id}` - 복용 스케줄 수정
- `DELETE /api/medication-schedules/{id}` - 복용 스케줄 삭제

## 데이터베이스 정보

- **호스트**: localhost:3306
- **데이터베이스**: alyak
- **사용자**: alyak_user
- **비밀번호**: alyak_password

## 개발 환경 설정

### 로컬 개발
```bash
# MySQL 실행 (Docker)
docker run -d --name mysql-dev \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=alyak \
  -e MYSQL_USER=alyak_user \
  -e MYSQL_PASSWORD=alyak_password \
  -p 3306:3306 \
  mysql:8.0

# 애플리케이션 실행
./mvnw spring-boot:run
```

### 환경 변수 설정
- `SPRING_DATASOURCE_URL`: 데이터베이스 연결 URL
- `SPRING_DATASOURCE_USERNAME`: 데이터베이스 사용자명
- `SPRING_DATASOURCE_PASSWORD`: 데이터베이스 비밀번호
- `SERVER_PORT`: 서버 포트 (기본값: 8080) 