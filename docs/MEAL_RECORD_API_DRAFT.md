# 3주차 식단 기록 기능 API 명세 초안

이 문서는 3주차 식단 CRUD 구현을 바로 시작할 수 있도록 작성한 초안입니다. 실제 구현 시 팀 회의에서 필드 추가/삭제를 확정합니다.

## 1. MealRecord 데이터 구조 초안

| 필드명 | 타입 | 필수 | 설명 |
|---|---:|---:|---|
| `id` | number | 응답 | 식단 기록 id |
| `profileId` | number | O | 프로필 id. 프론트는 localStorage의 `profileId` 사용 |
| `mealDate` | string | O | 식사 날짜, `YYYY-MM-DD` |
| `mealType` | enum | O | `BREAKFAST`, `LUNCH`, `DINNER`, `SNACK` |
| `foodName` | string | O | 음식명 |
| `calories` | number | O | 섭취 칼로리, kcal |
| `carbs` | number | O | 탄수화물, g |
| `protein` | number | O | 단백질, g |
| `fat` | number | O | 지방, g |
| `memo` | string | 선택 | 메모 |
| `createdAt` | string | 응답 | 생성 시각 |
| `updatedAt` | string | 응답 | 수정 시각 |

## 2. Enum 값

### mealType

- `BREAKFAST`
- `LUNCH`
- `DINNER`
- `SNACK`

## 3. 식단 기록 생성

```http
POST /api/meal-records
Content-Type: application/json
```

```json
{
  "profileId": 1,
  "mealDate": "2026-05-27",
  "mealType": "BREAKFAST",
  "foodName": "현미밥과 닭가슴살",
  "calories": 520,
  "carbs": 65,
  "protein": 38,
  "fat": 12,
  "memo": "아침 식사"
}
```

### Response 201

```json
{
  "id": 1,
  "profileId": 1,
  "mealDate": "2026-05-27",
  "mealType": "BREAKFAST",
  "foodName": "현미밥과 닭가슴살",
  "calories": 520,
  "carbs": 65,
  "protein": 38,
  "fat": 12,
  "memo": "아침 식사",
  "createdAt": "2026-05-27T15:30:00",
  "updatedAt": "2026-05-27T15:30:00"
}
```

## 4. 특정 날짜 식단 목록 조회

```http
GET /api/meal-records?profileId=1&mealDate=2026-05-27
```

### Response 200

```json
[
  {
    "id": 1,
    "profileId": 1,
    "mealDate": "2026-05-27",
    "mealType": "BREAKFAST",
    "foodName": "현미밥과 닭가슴살",
    "calories": 520,
    "carbs": 65,
    "protein": 38,
    "fat": 12,
    "memo": "아침 식사",
    "createdAt": "2026-05-27T15:30:00",
    "updatedAt": "2026-05-27T15:30:00"
  }
]
```

## 5. 식단 기록 단건 조회

```http
GET /api/meal-records/{id}
```

## 6. 식단 기록 수정

```http
PUT /api/meal-records/{id}
Content-Type: application/json
```

```json
{
  "mealDate": "2026-05-27",
  "mealType": "LUNCH",
  "foodName": "닭가슴살 샐러드",
  "calories": 430,
  "carbs": 35,
  "protein": 45,
  "fat": 14,
  "memo": "점심 식사"
}
```

## 7. 식단 기록 삭제

```http
DELETE /api/meal-records/{id}
```

### Response 204

응답 본문 없음.

## 8. 하루 섭취량 요약 조회

대시보드 연동을 위해 3주차 구현 후보로 둡니다.

```http
GET /api/meal-records/summary?profileId=1&mealDate=2026-05-27
```

### Response 200

```json
{
  "profileId": 1,
  "mealDate": "2026-05-27",
  "consumedCalories": 1450,
  "consumedCarbs": 180,
  "consumedProtein": 90,
  "consumedFat": 45
}
```

## 9. 3주차 구현 우선순위 제안

1. `MealRecord` Entity / Repository / DTO 작성
2. `POST /api/meal-records` 생성 API 구현
3. `GET /api/meal-records?profileId=&mealDate=` 날짜별 목록 조회 구현
4. DashboardPage에서 요약 API 또는 localStorage mock 데이터를 읽는 구조로 연결
5. 수정/삭제 API 구현
