# FitCal Manager

개인 맞춤형 **식단 및 칼로리 관리 매니저** 프로젝트입니다.

사용자의 신체 정보와 목표를 입력받아 하루 권장 칼로리와 탄단지 권장량을 계산하고, 이후 식단 기록과 대시보드 기능으로 확장할 예정입니다.

---

## 기술 스택

### Frontend

- React
- Vite
- JavaScript
- Tailwind CSS
- Axios
- React Router
- Recharts

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Validation
- Lombok
- MySQL Connector

### Database

- MySQL 8.4
- Docker Container로 실행

### DevOps / Environment

- Docker
- Docker Compose
- GitHub

---

## 실행 방법

### 1. Backend + MySQL 실행

프로젝트 루트에서 실행합니다.

```bash
docker compose up -d --build
```

Health Check:

```bash
curl http://localhost:8080/api/health
```

정상 응답:

```json
{"status":"OK"}
```

### 2. Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

브라우저에서 `http://localhost:5173`으로 접속합니다.

---

## 2주차 핵심 사용자 흐름

```text
/profile 프로필 입력
→ POST /api/profiles 프로필 저장
→ POST /api/nutrition/calculate 권장량 계산
→ /result 실제 계산 결과 표시
→ localStorage nutritionResult 저장
→ /dashboard 목표 칼로리와 탄단지 목표 반영
```

---

## API 명세 및 문서

- [2주차 API 명세](docs/API_SPEC_WEEK2.md)
- [2주차 상준 담당 통합 체크리스트](docs/WEEK2_INTEGRATION_CHECKLIST.md)
- [3주차 식단 기록 API 명세 초안](docs/MEAL_RECORD_API_DRAFT.md)
- [AI 활용 기록 취합 양식](docs/AI_USAGE_LOG_TEMPLATE.md)

---

## 프로젝트 구조

```text
fitcal-manager/
├─ backend/          # Spring Boot 백엔드
├─ frontend/         # React 프론트엔드
├─ docs/             # API 명세, 통합 체크리스트, 3주차 설계 초안
├─ docker-compose.yml
├─ README.md
└─ .gitignore
```
