# 2주차 상준 담당 통합 체크리스트

## 목표

상준 담당 역할은 PM / 백엔드 / 통합 관리 / 개발환경 관리입니다. 이번 주 핵심은 develop 브랜치에서 백엔드와 프론트가 함께 실행되고, 프로필 저장 → 권장량 계산 → 결과 표시 → 대시보드 반영 흐름이 실제 API 필드명으로 연결되는지 확인하는 것입니다.

## 체크리스트

| 구분 | 항목 | 확인 방법 | 상태 |
|---|---|---|---|
| 브랜치 | develop 최신 상태 기준 작업 | GitHub develop 또는 최신 zip 기준 확인 | 확인 필요 |
| Docker | backend + mysql 실행 | `docker compose up -d --build` | 확인 필요 |
| Backend | Health Check | `GET /api/health` | 확인 필요 |
| Backend | 프로필 저장 | `POST /api/profiles` | 확인 필요 |
| Backend | 프로필 목록 조회 | `GET /api/profiles` | 확인 필요 |
| Backend | 프로필 단건 조회 | `GET /api/profiles/{id}` | 확인 필요 |
| Backend | 권장량 계산 | `POST /api/nutrition/calculate` | 확인 필요 |
| Frontend | 로컬 실행 | `cd frontend && npm install && npm run dev` | 확인 필요 |
| Frontend | API 호출 성공 | `/profile`에서 저장/계산 후 `/result` 이동 | 확인 필요 |
| Frontend | localStorage 저장 | `profileId`, `nutritionResult` 확인 | 확인 필요 |
| Frontend | 대시보드 반영 | `/dashboard`에서 계산 결과 기반 목표 표시 | 확인 필요 |
| 문서 | API 명세 공유 | `docs/API_SPEC_WEEK2.md` | 작성 완료 |
| 문서 | MealRecord API 초안 | `docs/MEAL_RECORD_API_DRAFT.md` | 작성 완료 |

## 로컬 실행 순서

### 1. Docker backend + mysql 실행

```bash
docker compose up -d --build
```

### 2. Health Check

```bash
curl http://localhost:8080/api/health
```

기대 응답:

```json
{"status":"OK"}
```

### 3. 프로필 저장 API

```bash
curl -X POST http://localhost:8080/api/profiles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "고상준",
    "age": 27,
    "heightCm": 172,
    "weightKg": 70,
    "gender": "MALE",
    "activityLevel": "NORMAL",
    "goalType": "DIET"
  }'
```

### 4. 권장량 계산 API

```bash
curl -X POST http://localhost:8080/api/nutrition/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "age": 27,
    "heightCm": 172,
    "weightKg": 70,
    "gender": "MALE",
    "activityLevel": "NORMAL",
    "goalType": "DIET"
  }'
```

응답에 아래 필드가 모두 있어야 합니다.

- `goalType`
- `goalLabel`
- `bmr`
- `tdee`
- `targetCalories`
- `targetCarbs`
- `targetProtein`
- `targetFat`
- `message`

### 5. 프론트 실행

```bash
cd frontend
npm install
npm run dev
```

브라우저에서 `http://localhost:5173/profile` 접속 후 프로필 입력 → 권장량 계산하기를 실행합니다.

### 6. 브라우저 localStorage 확인

개발자도구 Console에서 확인합니다.

```js
localStorage.getItem("profileId")
JSON.parse(localStorage.getItem("nutritionResult"))
```

## PR 관리 기준

- feature 브랜치 PR은 `develop`으로 보냅니다.
- `main`은 최종 제출 안정 버전으로 둡니다.
- PR 머지 전 `Files changed`에서 의도하지 않은 대량 변경, `node_modules`, `build`, `dist`가 포함되지 않았는지 확인합니다.
- 가능하면 `Squash and merge`로 기능 단위 커밋을 정리합니다.

## 공유 문구 예시

팀 공지에 아래 내용을 공유하면 됩니다.

```text
2주차 API 필드명은 docs/API_SPEC_WEEK2.md 기준으로 고정합니다.
프로필 저장 요청은 name, age, heightCm, weightKg, gender, activityLevel, goalType을 사용하고,
권장량 계산 요청은 age, heightCm, weightKg, gender, activityLevel, goalType을 사용합니다.
계산 응답은 goalType, goalLabel, bmr, tdee, targetCalories, targetCarbs, targetProtein, targetFat, message를 사용합니다.
프론트에서는 profileId와 nutritionResult를 localStorage에 저장해주세요.
```
