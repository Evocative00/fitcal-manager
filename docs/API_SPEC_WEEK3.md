# FitCal Manager 3주차 API 명세

기준 브랜치: `develop`

3주차 API는 기존 1~2주차 프로필/권장량 계산 흐름을 유지하고, 식단 기록 CRUD와 하루 섭취량 요약 API를 추가한다.

## 1. 공통

- Base URL: `http://localhost:8080/api`
- Frontend baseURL: `import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api"`
- Content-Type: `application/json`

## 2. 프로필 저장

```http
POST /api/profiles
```

### Request

```json
{
  "name": "홍길동",
  "age": 24,
  "heightCm": 175,
  "weightKg": 70,
  "gender": "MALE",
  "activityLevel": "NORMAL",
  "goalType": "DIET"
}
```

### 주요 필드

| 필드명 | 타입 | 필수 | 설명 |
|---|---:|---:|---|
| `name` | string | O | 사용자 이름 |
| `age` | number | O | 나이 |
| `heightCm` | number | O | 키, cm |
| `weightKg` | number | O | 몸무게, kg |
| `gender` | enum | O | `MALE`, `FEMALE` |
| `activityLevel` | enum | O | `LOW`, `NORMAL`, `HIGH`, `VERY_HIGH` |
| `goalType` | enum | O | `DIET`, `MAINTAIN`, `BULK_UP`, `HIGH_PROTEIN` |

## 3. 권장량 계산

```http
POST /api/nutrition/calculate
```

### Request

```json
{
  "age": 24,
  "heightCm": 175,
  "weightKg": 70,
  "gender": "MALE",
  "activityLevel": "NORMAL",
  "goalType": "DIET"
}
```

### Response

```json
{
  "goalType": "DIET",
  "goalLabel": "다이어트",
  "bmr": 1695.7,
  "tdee": 2628.3,
  "targetCalories": 2128.3,
  "targetCarbs": 212.8,
  "targetProtein": 159.6,
  "targetFat": 70.9,
  "message": "다이어트 목표 기준으로 권장량을 계산했습니다."
}
```

## 4. 식단 기록 생성

```http
POST /api/meals
```

### Request

```json
{
  "profileId": 1,
  "foodName": "현미밥과 닭가슴살",
  "mealType": "BREAKFAST",
  "calories": 520,
  "proteinG": 38,
  "carbsG": 65,
  "fatG": 12,
  "recordedDate": "2026-05-27"
}
```

### Response 201

```json
{
  "id": 1,
  "profileId": 1,
  "foodName": "현미밥과 닭가슴살",
  "mealType": "BREAKFAST",
  "mealTypeLabel": "아침",
  "calories": 520,
  "proteinG": 38,
  "carbsG": 65,
  "fatG": 12,
  "recordedDate": "2026-05-27",
  "createdAt": "2026-05-27T15:30:00"
}
```

## 5. 특정 날짜 식단 목록 조회

```http
GET /api/meals?profileId={profileId}&date={yyyy-MM-dd}
```

### 예시

```http
GET /api/meals?profileId=1&date=2026-05-27
```

### Response 200

```json
[
  {
    "id": 1,
    "profileId": 1,
    "foodName": "현미밥과 닭가슴살",
    "mealType": "BREAKFAST",
    "mealTypeLabel": "아침",
    "calories": 520,
    "proteinG": 38,
    "carbsG": 65,
    "fatG": 12,
    "recordedDate": "2026-05-27",
    "createdAt": "2026-05-27T15:30:00"
  }
]
```

## 6. 식단 기록 단건 조회

```http
GET /api/meals/{id}
```

### Response 200

```json
{
  "id": 1,
  "profileId": 1,
  "foodName": "현미밥과 닭가슴살",
  "mealType": "BREAKFAST",
  "mealTypeLabel": "아침",
  "calories": 520,
  "proteinG": 38,
  "carbsG": 65,
  "fatG": 12,
  "recordedDate": "2026-05-27",
  "createdAt": "2026-05-27T15:30:00"
}
```

## 7. 식단 기록 수정

```http
PUT /api/meals/{id}
```

### Request

```json
{
  "profileId": 1,
  "foodName": "닭가슴살 샐러드",
  "mealType": "LUNCH",
  "calories": 430,
  "proteinG": 45,
  "carbsG": 35,
  "fatG": 14,
  "recordedDate": "2026-05-27"
}
```

### Response 200

```json
{
  "id": 1,
  "profileId": 1,
  "foodName": "닭가슴살 샐러드",
  "mealType": "LUNCH",
  "mealTypeLabel": "점심",
  "calories": 430,
  "proteinG": 45,
  "carbsG": 35,
  "fatG": 14,
  "recordedDate": "2026-05-27",
  "createdAt": "2026-05-27T15:30:00"
}
```

## 8. 식단 기록 삭제

```http
DELETE /api/meals/{id}
```

### Response 204

응답 본문 없음.

## 9. 하루 섭취량 요약 조회

대시보드가 실제 섭취량을 표시하기 위해 사용하는 API다.

```http
GET /api/meals/summary?profileId={profileId}&date={yyyy-MM-dd}
```

### 예시

```http
GET /api/meals/summary?profileId=1&date=2026-05-27
```

### Response 200

```json
{
  "profileId": 1,
  "date": "2026-05-27",
  "consumedCalories": 950,
  "consumedCarbs": 100,
  "consumedProtein": 83,
  "consumedFat": 26,
  "mealCount": 2
}
```

식단 기록이 없는 날짜는 합산 필드와 `mealCount`를 모두 0으로 반환한다.

## 10. Enum 고정값

### gender

- `MALE`
- `FEMALE`

### activityLevel

- `LOW`
- `NORMAL`
- `HIGH`
- `VERY_HIGH`

### goalType

- `DIET`
- `MAINTAIN`
- `BULK_UP`
- `HIGH_PROTEIN`

### mealType

- `BREAKFAST`
- `LUNCH`
- `DINNER`
- `SNACK`

## 11. 프론트 저장 key

- 프로필 저장 응답의 `id`: `localStorage.setItem("profileId", String(profileResponse.data.id))`
- 권장량 계산 응답: `localStorage.setItem("nutritionResult", JSON.stringify(nutritionResponse.data))`

## 12. 오류 응답 공통 형태

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "입력값이 올바르지 않습니다.",
  "details": [
    "foodName: 음식명은 필수입니다."
  ]
}
```
