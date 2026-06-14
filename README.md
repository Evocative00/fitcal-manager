# FitCal Manager

개인 맞춤형 **식단 및 칼로리 관리 매니저** 프로젝트입니다.

사용자의 신체 정보와 목표를 입력받아 하루 권장 칼로리와 탄단지 권장량을 계산하고, 식단 기록을 대시보드에 연결해 목표 대비 실제 섭취량을 확인할 수 있습니다.

---

## 핵심 기능

- 프로필 저장: 이름, 나이, 키, 몸무게, 성별, 활동량, 목표 저장
- 권장량 계산: Strategy 패턴으로 목표별 칼로리/탄단지 계산
- 식단 기록: 날짜별 식단 생성, 조회, 수정, 삭제
- 하루 섭취량 요약: 칼로리, 탄수화물, 단백질, 지방 합산
- 대시보드: 목표 대비 실제 섭취량, 진행률, 피드백, 탄단지 차트 표시

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
- MySQL Connector
- H2 테스트 DB

### Database / Environment

- MySQL 8.4
- Docker Compose
- GitHub Flow

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

## 최종 사용자 흐름

```text
/ 홈 화면
→ /profile 프로필 입력
→ POST /api/profiles 프로필 저장
→ POST /api/nutrition/calculate 권장량 계산
→ /result 실제 계산 결과 표시
→ /meals 날짜별 식단 등록/조회/수정/삭제
→ /dashboard 실제 섭취량과 목표 대비 진행률 확인
```

프론트는 다음 localStorage key를 사용합니다.

```text
profileId
nutritionResult
```

---

## 주요 API

### 프로필

```http
POST /api/profiles
GET /api/profiles
GET /api/profiles/{id}
```

### 권장량 계산

```http
POST /api/nutrition/calculate
```

### 식단 기록

```http
POST /api/meals
GET /api/meals?profileId={profileId}&date={yyyy-MM-dd}
GET /api/meals/{id}
PUT /api/meals/{id}
DELETE /api/meals/{id}
GET /api/meals/summary?profileId={profileId}&date={yyyy-MM-dd}
```

---

## API 명세 및 문서

- [2주차 API 명세](docs/API_SPEC_WEEK2.md)
- [3주차 API 명세](docs/API_SPEC_WEEK3.md)
- [식단 기록 API 명세](docs/MEAL_RECORD_API_DRAFT.md)
- [2주차 통합 체크리스트](docs/WEEK2_INTEGRATION_CHECKLIST.md)
- [3주차 통합 체크리스트](docs/WEEK3_INTEGRATION_CHECKLIST.md)
- [4주차 최종 체크리스트](docs/WEEK4_FINAL_CHECKLIST.md)
- [AI 활용 기록 취합 양식](docs/AI_USAGE_LOG_TEMPLATE.md)

---

## 테스트

### Frontend

```bash
cd frontend
npm install
npm run lint
npm run build
```

### Backend

```bash
cd backend
./gradlew test
```

Windows:

```powershell
cd backend
.\gradlew.bat test
```

---

## 프로젝트 구조

```text
fitcal-manager/
├─ backend/          # Spring Boot 백엔드
├─ frontend/         # React 프론트엔드
├─ docs/             # API 명세, 통합 체크리스트, AI 활용 기록 양식
├─ docker-compose.yml
├─ README.md
└─ .gitignore
```
