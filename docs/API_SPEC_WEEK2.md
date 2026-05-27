# FitCal Manager 2주차 API 명세

이 문서는 develop 브랜치 기준으로 프론트엔드와 백엔드가 공통으로 사용할 API 경로, request/response 필드명, enum 값을 고정하기 위한 문서입니다.

## 공통 규칙

- Base URL: `http://localhost:8080/api`
- Request/Response 형식: JSON
- 프론트 입력 state 이름이 다르더라도 백엔드로 전송할 때는 이 문서의 필드명을 사용합니다.
- 숫자 입력값은 문자열이 아니라 Number 타입으로 변환해서 전송합니다.
- enum 값은 대문자 고정값만 사용합니다.

## Enum 값

### gender

| 값 | 의미 |
|---|---|
| `MALE` | 남성 |
| `FEMALE` | 여성 |

### activityLevel

| 값 | 의미 |
|---|---|
| `LOW` | 낮음 |
| `NORMAL` | 보통 |
| `HIGH` | 높음 |
| `VERY_HIGH` | 매우 높음 |

### goalType

| 값 | 의미 |
|---|---|
| `DIET` | 다이어트 |
| `MAINTAIN` | 유지 |
| `BULK_UP` | 벌크업 |
| `HIGH_PROTEIN` | 고단백 |

## 1. Health Check

### Request

```http
GET /api/health
```

### Response 200

```json
{
  "status": "OK"
}
```

## 2. 프로필 저장

### Request

```http
POST /api/profiles
Content-Type: application/json
```

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

### 필드 설명

| 필드명 | 타입 | 필수 | 설명 |
|---|---:|---:|---|
| `name` | string | O | 사용자 이름 |
| `age` | number | O | 나이 |
| `heightCm` | number | O | 키, cm 단위 |
| `weightKg` | number | O | 몸무게, kg 단위 |
| `gender` | enum | O | `MALE`, `FEMALE` |
| `activityLevel` | enum | O | `LOW`, `NORMAL`, `HIGH`, `VERY_HIGH` |
| `goalType` | enum | O | `DIET`, `MAINTAIN`, `BULK_UP`, `HIGH_PROTEIN` |

### Response 201

```json
{
  "id": 1,
  "name": "홍길동",
  "age": 24,
  "heightCm": 175.0,
  "weightKg": 70.0,
  "gender": "MALE",
  "activityLevel": "NORMAL",
  "goalType": "DIET",
  "createdAt": "2026-05-27T15:30:00",
  "updatedAt": "2026-05-27T15:30:00"
}
```

### 프론트 저장 규칙

프로필 저장 API 응답에서 받은 `id`는 localStorage의 `profileId`에 저장합니다.

```js
localStorage.setItem("profileId", String(profileResponse.data.id));
```

## 3. 프로필 목록 조회

### Request

```http
GET /api/profiles
```

### Response 200

```json
[
  {
    "id": 1,
    "name": "홍길동",
    "age": 24,
    "heightCm": 175.0,
    "weightKg": 70.0,
    "gender": "MALE",
    "activityLevel": "NORMAL",
    "goalType": "DIET",
    "createdAt": "2026-05-27T15:30:00",
    "updatedAt": "2026-05-27T15:30:00"
  }
]
```

## 4. 프로필 단건 조회

### Request

```http
GET /api/profiles/{id}
```

### Response 200

```json
{
  "id": 1,
  "name": "홍길동",
  "age": 24,
  "heightCm": 175.0,
  "weightKg": 70.0,
  "gender": "MALE",
  "activityLevel": "NORMAL",
  "goalType": "DIET",
  "createdAt": "2026-05-27T15:30:00",
  "updatedAt": "2026-05-27T15:30:00"
}
```

### Response 404

```json
{
  "timestamp": "2026-05-27T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "프로필을 찾을 수 없습니다. id=9999",
  "details": []
}
```

## 5. 권장량 계산

### Request

```http
POST /api/nutrition/calculate
Content-Type: application/json
```

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

### 필드 설명

| 필드명 | 타입 | 필수 | 설명 |
|---|---:|---:|---|
| `age` | number | O | 나이 |
| `heightCm` | number | O | 키, cm 단위 |
| `weightKg` | number | O | 몸무게, kg 단위 |
| `gender` | enum | O | `MALE`, `FEMALE` |
| `activityLevel` | enum | O | `LOW`, `NORMAL`, `HIGH`, `VERY_HIGH` |
| `goalType` | enum | O | `DIET`, `MAINTAIN`, `BULK_UP`, `HIGH_PROTEIN` |

### Response 200

아래 필드는 프론트에서 사용하는 고정 필드입니다.

```json
{
  "goalType": "DIET",
  "goalLabel": "다이어트 (체지방 감량)",
  "bmr": 1700.0,
  "tdee": 2635.0,
  "targetCalories": 2135.0,
  "targetCarbs": 213.5,
  "targetProtein": 160.1,
  "targetFat": 71.2,
  "message": "하루 500kcal 적자로 주 0.5kg 체지방 감량 목표"
}
```

### 프론트 저장 규칙

권장량 계산 API 응답 결과는 localStorage의 `nutritionResult`에 저장합니다.

```js
localStorage.setItem("nutritionResult", JSON.stringify(nutritionResponse.data));
```

## 6. 공통 에러 응답

### 입력값 검증 실패 400

```json
{
  "timestamp": "2026-05-27T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "입력값이 올바르지 않습니다.",
  "details": [
    "age: 나이는 1 이상이어야 합니다."
  ]
}
```

### JSON 형식 또는 enum 값 오류 400

```json
{
  "timestamp": "2026-05-27T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "요청 JSON 형식 또는 enum 값이 올바르지 않습니다.",
  "details": []
}
```

## 7. 프론트 필드 변환 기준

프론트 내부 state 이름을 임의로 쓰더라도 API 호출 직전에는 아래처럼 변환합니다.

| 프론트 내부 이름 예시 | API 전송 필드명 |
|---|---|
| `height` | `heightCm` |
| `weight` | `weightKg` |
| `goal` | `goalType` |

2주차 develop 기준 ProfileFormPage에서는 처음부터 `heightCm`, `weightKg`, `goalType`을 state 필드명으로 사용합니다.
