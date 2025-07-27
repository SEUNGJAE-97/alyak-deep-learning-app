# Alyak API Server

복약 관리 시스템을 위한 Spring Boot API 서버입니다.

## 기술 스택

- **Spring Boot 3.5.3** (Java 17)
- **Spring Data JPA**
- **MySQL 8.0**
- **Docker & Docker Compose**

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

## 프로젝트 구조

```
src/main/java/com/github/seungjae97/alyak/alyakapiserver/
├── controller/     # REST API 컨트롤러
├── service/        # 비즈니스 로직
├── repository/     # 데이터 접근 계층
├── entity/         # JPA 엔티티
└── dto/           # 데이터 전송 객체
``` 