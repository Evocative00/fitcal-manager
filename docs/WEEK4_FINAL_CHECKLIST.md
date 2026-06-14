# FitCal Manager 4주차 마무리 체크리스트

기준 브랜치: `develop`

4주차 목표는 새로운 핵심 도메인을 추가하는 것보다, 1~3주차에 구현한 사용자 흐름을 안정화하고 UI/UX를 개선해 최종 제출 가능한 상태로 정리하는 것이다.

## 1. 최종 사용자 흐름

- [ ] `/` 랜딩 페이지에서 프로젝트 목적과 진행 흐름을 이해할 수 있다.
- [ ] `/profile`에서 프로필을 입력하고 권장량 계산을 실행할 수 있다.
- [ ] `POST /api/profiles` 호출 후 `localStorage.profileId`가 저장된다.
- [ ] `POST /api/nutrition/calculate` 호출 후 `localStorage.nutritionResult`가 저장된다.
- [ ] `/result`에서 실제 권장 칼로리와 탄단지 목표를 확인할 수 있다.
- [ ] `/meals`에서 식단 등록, 조회, 수정, 삭제가 가능하다.
- [ ] `/dashboard`에서 목표 대비 실제 섭취량과 피드백을 확인할 수 있다.

## 2. API 고정값 유지

- [ ] Profile request 필드명: `name`, `age`, `heightCm`, `weightKg`, `gender`, `activityLevel`, `goalType`
- [ ] Nutrition request 필드명: `age`, `heightCm`, `weightKg`, `gender`, `activityLevel`, `goalType`
- [ ] Meal endpoint: `/api/meals`
- [ ] Meal request 필드명: `profileId`, `foodName`, `mealType`, `calories`, `proteinG`, `carbsG`, `fatG`, `recordedDate`
- [ ] Meal summary response 필드명: `profileId`, `date`, `consumedCalories`, `consumedCarbs`, `consumedProtein`, `consumedFat`, `mealCount`

## 3. UI/UX 점검

- [ ] 헤더 내비게이션에서 현재 페이지가 구분된다.
- [ ] 홈 화면에서 전체 서비스 흐름이 보인다.
- [ ] 프로필 입력 화면에 API 호출 흐름과 입력 필드 설명이 보인다.
- [ ] 결과 화면에서 다음 행동(`/meals`, `/dashboard`)으로 이동할 수 있다.
- [ ] 식단 기록 화면에 날짜 선택, 추천 식단 버튼, 하루 요약 카드가 있다.
- [ ] 대시보드에 목표/섭취/식단 수가 요약되고 탄단지 차트가 보인다.
- [ ] 오류, 빈 상태, 로딩 상태가 사용자가 이해할 수 있는 문구로 표시된다.

## 4. 테스트 명령

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

### Docker 통합 실행

```bash
docker compose up -d --build
curl http://localhost:8080/api/health
```

## 5. 최종 제출 전 정리

- [ ] `.git`, `.idea`, `node_modules`, `frontend/dist`, `backend/build`, `backend/.gradle`은 제출 zip에서 제외한다.
- [ ] PR base는 `develop`으로 유지한다.
- [ ] 최종 안정 버전만 `main`으로 병합한다.
- [ ] README의 실행 방법과 실제 프로젝트 구조가 일치한다.
- [ ] AI 활용 기록을 팀원별로 취합한다.
