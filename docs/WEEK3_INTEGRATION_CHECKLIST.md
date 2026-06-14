# FitCal Manager 3주차 통합 체크리스트

기준 브랜치: `develop`

## 1. 브랜치 / PR 확인

- [ ] feature 브랜치가 `develop`에서 생성되었는가?
- [ ] PR base가 `develop`인가?
- [ ] PR compare가 각자 feature 브랜치인가?
- [ ] `main`으로 직접 PR을 만들지 않았는가?
- [ ] PR 생성 전 `develop` 최신 변경사항을 반영했는가?

## 2. 백엔드 API 확인

### 공통

- [ ] `docker compose up -d --build` 성공
- [ ] `GET http://localhost:8080/api/health` 응답 `{ "status": "OK" }`

### 프로필 / 권장량 계산

- [ ] `POST /api/profiles` 성공
- [ ] `GET /api/profiles` 성공
- [ ] `GET /api/profiles/{id}` 성공
- [ ] `POST /api/nutrition/calculate` 성공
- [ ] 응답 필드가 `targetCalories`, `targetCarbs`, `targetProtein`, `targetFat`로 유지되는가?

### 식단 기록 CRUD

- [ ] `POST /api/meals` 성공
- [ ] `GET /api/meals?profileId={profileId}&date={yyyy-MM-dd}` 성공
- [ ] `GET /api/meals/{id}` 성공
- [ ] `PUT /api/meals/{id}` 성공
- [ ] `DELETE /api/meals/{id}` 성공
- [ ] endpoint가 `/api/meal-records`가 아니라 `/api/meals`인가?
- [ ] 날짜 필드가 `mealDate`가 아니라 `recordedDate`인가?
- [ ] 영양소 필드가 `proteinG`, `carbsG`, `fatG`인가?

### 하루 섭취량 요약

- [ ] `GET /api/meals/summary?profileId={profileId}&date={yyyy-MM-dd}` 성공
- [ ] `consumedCalories` 합산 정상
- [ ] `consumedCarbs` 합산 정상
- [ ] `consumedProtein` 합산 정상
- [ ] `consumedFat` 합산 정상
- [ ] `mealCount` 정상
- [ ] 식단이 없는 날짜는 합계 0, `mealCount` 0 반환
- [ ] 없는 `profileId`는 404 반환

## 3. 프론트 화면 확인

### 프로필 / 결과

- [ ] `/profile`에서 프로필 입력 가능
- [ ] 프로필 저장 후 `localStorage.profileId` 저장
- [ ] 권장량 계산 후 `localStorage.nutritionResult` 저장
- [ ] `/result`에서 실제 계산 결과 표시

### 식단 기록

- [ ] Header navigation에 `식단 기록` 링크 표시
- [ ] `/meals` 접속 가능
- [ ] profileId가 없으면 `/profile` 이동 안내 표시
- [ ] 날짜 선택 가능
- [ ] 식단 등록 성공
- [ ] 식단 목록 조회 성공
- [ ] 식단 수정 성공
- [ ] 식단 삭제 성공
- [ ] 등록/수정/삭제 후 목록 새로고침

### 대시보드

- [ ] `/dashboard`에서 `nutritionResult`를 읽어 목표 칼로리와 탄단지 표시
- [ ] `/dashboard`에서 `profileId`를 읽어 meal summary API 호출
- [ ] 식단 등록 전에는 0 kcal / 0 g 표시
- [ ] 식단 등록 후 새로고침 시 실제 섭취량 표시
- [ ] 목표 초과 시 초과 피드백 표시
- [ ] `/meals` 이동 버튼 표시
- [ ] 날짜 선택 시 해당 날짜 요약 조회

## 4. 테스트 명령

### 백엔드

```bash
cd backend
./gradlew test
```

Windows:

```powershell
cd backend
.\gradlew.bat test
```

### 프론트

```bash
cd frontend
npm install
npm run lint
npm run build
```

## 5. 통합 사용자 흐름

- [ ] `/profile`에서 프로필 입력
- [ ] `/result`에서 권장 칼로리와 탄단지 확인
- [ ] `/meals`에서 오늘 먹은 식단 등록
- [ ] `/meals`에서 등록한 식단 조회
- [ ] `/meals`에서 식단 수정
- [ ] `/meals`에서 식단 삭제
- [ ] `/dashboard`에서 실제 섭취량과 목표 대비 진행률 확인

## 6. Merge 전 위험 파일

아래 파일은 여러 담당자가 동시에 수정할 수 있으므로 PR 전 충돌 여부를 확인한다.

- `backend/src/main/java/com/syu/fitcal/controller/MealRecordController.java`
- `backend/src/main/java/com/syu/fitcal/service/MealRecordService.java`
- `backend/src/test/java/com/syu/fitcal/controller/MealRecordControllerTest.java`
- `frontend/src/App.jsx`
- `frontend/src/pages/DashboardPage.jsx`
