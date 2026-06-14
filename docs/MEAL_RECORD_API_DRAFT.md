# 3주차 식단 기록 기능 API 명세

이 문서는 2주차 초안에서 실제 3주차 코드 기준으로 수정한 식단 기록 API 명세다.

중요 고정값:

- 식단 API endpoint는 `/api/meals`를 사용한다.
- `/api/meal-records`는 사용하지 않는다.
- 날짜 필드는 `mealDate`가 아니라 `recordedDate`를 사용한다.
- 영양소 필드는 `protein`, `carbs`, `fat`이 아니라 `proteinG`, `carbsG`, `fatG`를 사용한다.
- 프론트는 `localStorage.profileId`를 `profileId`로 사용한다.

## 1. MealRecord 데이터 구조

| 필드명 | 타입 | 필수 | 설명 |
|---|---:|---:|---|
| `id` | number | 응답 | 식단 기록 id |
| `profileId` | number | O | 프로필 id |
| `foodName` | string | O | 음식명 |
| `mealType` | enum | O | `BREAKFAST`, `LUNCH`, `DINNER`, `SNACK` |
| `mealTypeLabel` | string | 응답 | `아침`, `점심`, `저녁`, `간식` |
| `calories` | number | O | 섭취 칼로리, kcal |
| `proteinG` | number | O | 단백질, g |
| `carbsG` | number | O | 탄수화물, g |
| `fatG` | number | O | 지방, g |
| `recordedDate` | string | O | 식단 기록 날짜, `YYYY-MM-DD` |
| `createdAt` | string | 응답 | 생성 시각 |

## 2. Enum 값

### mealType

- `BREAKFAST`
- `LUNCH`
- `DINNER`
- `SNACK`

## 3. 식단 기록 생성

```http
POST /api/meals
Content-Type: application/json
```

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

## 4. 특정 날짜 식단 목록 조회

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

## 5. 식단 기록 단건 조회

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

## 6. 식단 기록 수정

```http
PUT /api/meals/{id}
Content-Type: application/json
```

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

## 7. 식단 기록 삭제

```http
DELETE /api/meals/{id}
```

### Response 204

응답 본문 없음.

## 8. 하루 섭취량 요약 조회

대시보드 연동을 위해 사용한다.

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

식단이 없는 날짜는 아래처럼 0으로 반환한다.

```json
{
  "profileId": 1,
  "date": "2026-05-28",
  "consumedCalories": 0,
  "consumedCarbs": 0,
  "consumedProtein": 0,
  "consumedFat": 0,
  "mealCount": 0
}
```

## 9. 3주차 구현 우선순위

1. `PUT /api/meals/{id}` 구현
2. `GET /api/meals/summary?profileId=&date=` 구현
3. `/meals` 프론트 화면 구현
4. `/dashboard`에서 summary API 연동
5. 테스트와 통합 체크리스트 기준으로 검증
