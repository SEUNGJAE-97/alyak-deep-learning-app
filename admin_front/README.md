# admin_front

ALYAK의 관리자용 웹 대시보드입니다.  
라벨링 데이터 검수, 학습 작업 실행, 실시간 로그 모니터링, 모델 아카이브 조회를 위한 React/Vite 기반 프론트엔드입니다.

## 주요 기능

- 관리자 로그인
- 학습 서버 상태 확인
- 라벨링 항목 조회 및 박스 수정
- 항목 승인/반려 및 상태 일괄 변경
- 학습 작업 생성 및 진행률 확인
- 실시간 학습 로그 스트리밍
- 모델 아카이브 목록/상세/성능 비교

## 기술 스택

| 구분 | 내용 |
|------|------|
| 프레임워크 | React 19 |
| 언어 | TypeScript |
| 빌드 도구 | Vite |
| UI | Tailwind CSS 4, Lucide React, Motion |
| 차트 | Recharts |

## 화면 구성

- `Login` - 관리자 인증 및 학습 서버 상태 확인
- `Overview` - 라벨링 데이터셋 검수 및 박스 편집
- `Training` - 하이퍼파라미터 설정, 베이스 모델 선택, 학습 시작
- `TrainingLogs` - 학습 로그와 지표 시각화
- `Archives` - 모델 아카이브 및 성능 비교

## 백엔드 연동

이 프로젝트는 두 서버와 연동될 수 있습니다.

- Spring Boot API: 관리자 로그인, 라벨링, 아카이브, 작업 메타데이터
- FastAPI 학습 서버: 학습 로그 스트리밍

기본 로그인 API:

- `POST /api/auth/admin/login`

주요 관리자 API:

- `/api/admin/me`
- `/api/admin/labeling/*`
- `/api/admin/training/*`
- `/api/admin/archives/*`

상태 확인 및 스트림:

- Spring SSE: `/api/internal/training/jobs/system-status/stream`
- FastAPI SSE: `/train/jobs/{jobId}/logs/stream`

## 환경 변수

로컬 개발 시 `admin_front/.env.local` 파일을 만들어 사용하는 것을 권장합니다.

예시:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_FAST_API_BASE_URL=http://localhost:8001
```

설명:

- `VITE_API_BASE_URL` - Spring Boot 서버 주소
- `VITE_FAST_API_BASE_URL` - FastAPI 학습 로그 스트림 서버 주소

값이 없으면 기본값은 다음과 같습니다.

- Spring: `http://localhost:8080`
- FastAPI: `http://localhost:8001`

## 실행 방법

사전 요구 사항:

- Node.js
- 실행 중인 Spring Boot API
- 필요 시 실행 중인 FastAPI 학습 서버

설치 및 실행:

```bash
cd admin_front
npm install
npm run dev
```

기본 개발 서버 포트는 `3000`입니다.

## 빌드

```bash
cd admin_front
npm run build
```

## 참고 사항

- 관리자 로그인 토큰은 브라우저 `localStorage`에 저장됩니다.
- 학습 로그와 진행 상태도 일부 `localStorage`에 캐시됩니다.
- 현재 패키지에는 템플릿 유래 의존성이 일부 남아 있을 수 있으나, 실제 관리자 기능의 핵심 연동은 Spring/FastAPI 기반입니다.

## 관련 문서

- [모노레포 개요](../README.md)
- [Spring Boot API](../alyak-api-server/README.md)
