package com.syu.fitcal.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.fitcal.repository.MealRecordRepository;
import com.syu.fitcal.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MealRecord CRUD + Validation 통합 테스트.
 *
 * <h3>고정 테스트 데이터</h3>
 * <pre>
 *   닭가슴살(LUNCH)  — calories=165, protein=31, carbs=0,  fat=3.6
 *   현미밥  (DINNER) — calories=300, protein= 6, carbs=65, fat=1.5
 * </pre>
 */
@SpringBootTest
@AutoConfigureMockMvc
class MealRecordControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MealRecordRepository mealRecordRepository;
    @Autowired private UserProfileRepository userProfileRepository;

    private Long profileId;

    @BeforeEach
    void setUp() throws Exception {
        mealRecordRepository.deleteAll();
        userProfileRepository.deleteAll();
        profileId = createProfile();
    }

    // ══════════════════════════════════════════════════════════════
    // POST /api/meals
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/meals — 생성")
    class CreateMealRecord {

        @Test
        @DisplayName("정상 요청 → 201과 저장된 데이터를 반환한다")
        void success() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(chickenBody(profileId, null)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.profileId").value(profileId))
                    .andExpect(jsonPath("$.foodName").value("닭가슴살"))
                    .andExpect(jsonPath("$.mealType").value("LUNCH"))
                    .andExpect(jsonPath("$.mealTypeLabel").value("점심"))
                    .andExpect(jsonPath("$.calories").value(165.0))
                    .andExpect(jsonPath("$.proteinG").value(31.0))
                    .andExpect(jsonPath("$.carbsG").value(0.0))
                    .andExpect(jsonPath("$.fatG").value(3.6))
                    .andExpect(jsonPath("$.recordedDate").exists())
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("recordedDate를 명시하면 해당 날짜로 저장된다")
        void withExplicitDate() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(chickenBody(profileId, "2024-06-15")))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.recordedDate").value("2024-06-15"));
        }

        @Test
        @DisplayName("recordedDate를 생략하면 오늘 날짜로 저장된다")
        void recordedDateDefaultsToToday() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(chickenBody(profileId, null)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.recordedDate").value(LocalDate.now().toString()));
        }

        // ── 존재하지 않는 profileId ──────────────────────────────────

        @Test
        @DisplayName("존재하지 않는 profileId → 404와 오류 메시지를 반환한다")
        void nonExistentProfileId() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(chickenBody(99999L, null)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("프로필을 찾을 수 없습니다. id=99999"));
        }

        // ── 잘못된 mealType ──────────────────────────────────────────

        @Test
        @DisplayName("잘못된 mealType enum 값 → 400을 반환한다")
        void invalidMealType() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "profileId": %d,
                                      "foodName": "닭가슴살",
                                      "mealType": "MIDNIGHT_SNACK",
                                      "calories": 165.0,
                                      "proteinG": 31.0,
                                      "carbsG": 0.0,
                                      "fatG": 3.6
                                    }
                                    """.formatted(profileId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("요청 JSON 형식 또는 enum 값이 올바르지 않습니다."));
        }

        // ── foodName 검증 ────────────────────────────────────────────

        @Test
        @DisplayName("foodName 누락 → 400과 필드 오류를 반환한다")
        void missingFoodName() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "profileId": %d,
                                      "mealType": "LUNCH",
                                      "calories": 165.0,
                                      "proteinG": 31.0,
                                      "carbsG": 0.0,
                                      "fatG": 3.6
                                    }
                                    """.formatted(profileId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        }

        @Test
        @DisplayName("공백만 있는 foodName → 400을 반환한다")
        void blankFoodName() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "profileId": %d,
                                      "foodName": "   ",
                                      "mealType": "LUNCH",
                                      "calories": 165.0,
                                      "proteinG": 31.0,
                                      "carbsG": 0.0,
                                      "fatG": 3.6
                                    }
                                    """.formatted(profileId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("foodName")));
        }

        @Test
        @DisplayName("100자 초과 foodName → 400을 반환한다")
        void foodNameTooLong() throws Exception {
            String longName = "가".repeat(101);
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "profileId": %d,
                                      "foodName": "%s",
                                      "mealType": "LUNCH",
                                      "calories": 165.0,
                                      "proteinG": 31.0,
                                      "carbsG": 0.0,
                                      "fatG": 3.6
                                    }
                                    """.formatted(profileId, longName)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("foodName")));
        }

        // ── 음수·0 영양값 검증 ───────────────────────────────────────

        @Test
        @DisplayName("calories = 0 (inclusive=false) → 400을 반환한다")
        void zeroCalories() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(nutritionBody(profileId, 0.0, 31.0, 0.0, 3.6)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("calories")));
        }

        @Test
        @DisplayName("calories < 0 → 400을 반환한다")
        void negativeCalories() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(nutritionBody(profileId, -100.0, 31.0, 0.0, 3.6)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("calories")));
        }

        @Test
        @DisplayName("proteinG < 0 → 400을 반환한다")
        void negativeProteinG() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(nutritionBody(profileId, 165.0, -1.0, 0.0, 3.6)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("proteinG")));
        }

        @Test
        @DisplayName("carbsG < 0 → 400을 반환한다")
        void negativeCarbsG() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(nutritionBody(profileId, 165.0, 31.0, -1.0, 3.6)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("carbsG")));
        }

        @Test
        @DisplayName("fatG < 0 → 400을 반환한다")
        void negativeFatG() throws Exception {
            mockMvc.perform(post("/api/meals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(nutritionBody(profileId, 165.0, 31.0, 0.0, -1.0)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("fatG")));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // GET /api/meals
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/meals — 목록 조회")
    class FindMealRecords {

        @Test
        @DisplayName("날짜 미지정 → 프로필의 전체 기록을 반환한다")
        void allRecords() throws Exception {
            createMeal(profileId, "닭가슴살", "LUNCH",  165.0, 31.0,  0.0, 3.6, "2024-06-01");
            createMeal(profileId, "현미밥",   "DINNER", 300.0,  6.0, 65.0, 1.5, "2024-06-01");

            mockMvc.perform(get("/api/meals").param("profileId", profileId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("날짜 지정 → 해당 날짜의 기록만 반환한다")
        void byDate() throws Exception {
            createMeal(profileId, "닭가슴살", "LUNCH",  165.0, 31.0,  0.0, 3.6, "2024-06-01");
            createMeal(profileId, "현미밥",   "DINNER", 300.0,  6.0, 65.0, 1.5, "2024-06-02");

            mockMvc.perform(get("/api/meals")
                            .param("profileId", profileId.toString())
                            .param("date", "2024-06-01"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].foodName").value("닭가슴살"));
        }

        @Test
        @DisplayName("기록이 없는 프로필 → 빈 배열을 반환한다")
        void noRecords() throws Exception {
            mockMvc.perform(get("/api/meals").param("profileId", profileId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // GET /api/meals/summary
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/meals/summary — 일일 합산")
    class GetMealSummary {

        @Test
        @DisplayName("해당 날짜 기록의 영양소를 정확히 합산한다")
        void correctAggregation() throws Exception {
            // 165 + 300 = 465  /  31 + 6 = 37  /  0 + 65 = 65  /  3.6 + 1.5 = 5.1
            createMeal(profileId, "닭가슴살", "LUNCH",  165.0, 31.0,  0.0, 3.6, "2024-06-01");
            createMeal(profileId, "현미밥",   "DINNER", 300.0,  6.0, 65.0, 1.5, "2024-06-01");
            createMeal(profileId, "사과",     "SNACK",   57.0,  0.3, 15.0, 0.2, "2024-06-02"); // 다른 날짜 — 합산 제외

            mockMvc.perform(get("/api/meals/summary")
                            .param("profileId", profileId.toString())
                            .param("date", "2024-06-01"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.profileId").value(profileId))
                    .andExpect(jsonPath("$.date").value("2024-06-01"))
                    .andExpect(jsonPath("$.consumedCalories").value(465.0))
                    .andExpect(jsonPath("$.consumedProtein").value(37.0))
                    .andExpect(jsonPath("$.consumedCarbs").value(65.0))
                    .andExpect(jsonPath("$.consumedFat").value(5.1))
                    .andExpect(jsonPath("$.mealCount").value(2));
        }

        @Test
        @DisplayName("기록 없는 날짜 → 모든 영양소 0.0, mealCount 0을 반환한다")
        void noRecords_returnsZeroes() throws Exception {
            mockMvc.perform(get("/api/meals/summary")
                            .param("profileId", profileId.toString())
                            .param("date", "2024-06-01"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.consumedCalories").value(0.0))
                    .andExpect(jsonPath("$.consumedCarbs").value(0.0))
                    .andExpect(jsonPath("$.consumedProtein").value(0.0))
                    .andExpect(jsonPath("$.consumedFat").value(0.0))
                    .andExpect(jsonPath("$.mealCount").value(0));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // GET /api/meals/{id}
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/meals/{id} — 단건 조회")
    class FindMealRecord {

        @Test
        @DisplayName("정상 요청 → 200과 기록 데이터를 반환한다")
        void success() throws Exception {
            long mealId = createMeal(profileId, "닭가슴살", "LUNCH", 165.0, 31.0, 0.0, 3.6, "2024-06-01");

            mockMvc.perform(get("/api/meals/{id}", mealId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mealId))
                    .andExpect(jsonPath("$.foodName").value("닭가슴살"))
                    .andExpect(jsonPath("$.mealType").value("LUNCH"))
                    .andExpect(jsonPath("$.calories").value(165.0));
        }

        @Test
        @DisplayName("존재하지 않는 id → 404와 오류 메시지를 반환한다")
        void notFound() throws Exception {
            mockMvc.perform(get("/api/meals/{id}", 9999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("식사 기록을 찾을 수 없습니다. id=9999"));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // PUT /api/meals/{id}
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PUT /api/meals/{id} — 수정")
    class UpdateMealRecord {

        @Test
        @DisplayName("모든 필드를 전달하면 해당 값으로 교체된다")
        void allFieldsUpdated() throws Exception {
            long mealId = createMeal(profileId, "닭가슴살", "LUNCH", 165.0, 31.0, 0.0, 3.6, "2024-06-01");

            mockMvc.perform(put("/api/meals/{id}", mealId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "foodName": "연어 스테이크",
                                      "mealType": "DINNER",
                                      "calories": 250.0,
                                      "proteinG": 35.0,
                                      "carbsG": 0.0,
                                      "fatG": 12.0,
                                      "recordedDate": "2024-06-20"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mealId))
                    .andExpect(jsonPath("$.foodName").value("연어 스테이크"))
                    .andExpect(jsonPath("$.mealType").value("DINNER"))
                    .andExpect(jsonPath("$.calories").value(250.0))
                    .andExpect(jsonPath("$.proteinG").value(35.0))
                    .andExpect(jsonPath("$.fatG").value(12.0))
                    .andExpect(jsonPath("$.recordedDate").value("2024-06-20"));
        }

        @Test
        @DisplayName("recordedDate를 생략하면 기존 날짜가 유지된다")
        void datePreservedWhenNull() throws Exception {
            long mealId = createMeal(profileId, "닭가슴살", "LUNCH", 165.0, 31.0, 0.0, 3.6, "2024-06-01");

            mockMvc.perform(put("/api/meals/{id}", mealId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "foodName": "닭가슴살 (업데이트)",
                                      "mealType": "LUNCH",
                                      "calories": 170.0,
                                      "proteinG": 32.0,
                                      "carbsG": 0.0,
                                      "fatG": 3.8
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.foodName").value("닭가슴살 (업데이트)"))
                    .andExpect(jsonPath("$.recordedDate").value("2024-06-01")); // 기존 날짜 유지
        }

        @Test
        @DisplayName("존재하지 않는 id → 404와 오류 메시지를 반환한다")
        void notFound() throws Exception {
            mockMvc.perform(put("/api/meals/{id}", 9999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "foodName": "연어",
                                      "mealType": "LUNCH",
                                      "calories": 200.0,
                                      "proteinG": 25.0,
                                      "carbsG": 0.0,
                                      "fatG": 10.0
                                    }
                                    """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("식사 기록을 찾을 수 없습니다. id=9999"));
        }

        @Test
        @DisplayName("잘못된 mealType → 400을 반환한다")
        void invalidMealType() throws Exception {
            long mealId = createMeal(profileId, "닭가슴살", "LUNCH", 165.0, 31.0, 0.0, 3.6, null);

            mockMvc.perform(put("/api/meals/{id}", mealId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "foodName": "연어",
                                      "mealType": "WRONG_TYPE",
                                      "calories": 200.0,
                                      "proteinG": 25.0,
                                      "carbsG": 0.0,
                                      "fatG": 10.0
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("요청 JSON 형식 또는 enum 값이 올바르지 않습니다."));
        }

        @Test
        @DisplayName("calories < 0 → 400을 반환한다")
        void negativeCalories() throws Exception {
            long mealId = createMeal(profileId, "닭가슴살", "LUNCH", 165.0, 31.0, 0.0, 3.6, null);

            mockMvc.perform(put("/api/meals/{id}", mealId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "foodName": "잘못된 칼로리",
                                      "mealType": "LUNCH",
                                      "calories": -50.0,
                                      "proteinG": 10.0,
                                      "carbsG": 0.0,
                                      "fatG": 5.0
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("calories")));
        }

        @Test
        @DisplayName("calories = 0 (inclusive=false) → 400을 반환한다")
        void zeroCalories() throws Exception {
            long mealId = createMeal(profileId, "닭가슴살", "LUNCH", 165.0, 31.0, 0.0, 3.6, null);

            mockMvc.perform(put("/api/meals/{id}", mealId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "foodName": "0칼로리 식품",
                                      "mealType": "SNACK",
                                      "calories": 0.0,
                                      "proteinG": 0.0,
                                      "carbsG": 0.0,
                                      "fatG": 0.0
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[0]", containsString("calories")));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // DELETE /api/meals/{id}
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("DELETE /api/meals/{id} — 삭제")
    class DeleteMealRecord {

        @Test
        @DisplayName("정상 삭제 → 204를 반환하고, 이후 조회 시 404가 된다")
        void success() throws Exception {
            long mealId = createMeal(profileId, "닭가슴살", "LUNCH", 165.0, 31.0, 0.0, 3.6, null);

            mockMvc.perform(delete("/api/meals/{id}", mealId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/meals/{id}", mealId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("존재하지 않는 id → 404와 오류 메시지를 반환한다")
        void notFound() throws Exception {
            mockMvc.perform(delete("/api/meals/{id}", 9999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("식사 기록을 찾을 수 없습니다. id=9999"));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // 헬퍼 메서드
    // ══════════════════════════════════════════════════════════════

    private Long createProfile() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "테스트유저",
                                  "age": 25,
                                  "heightCm": 175.0,
                                  "weightKg": 70.0,
                                  "gender": "MALE",
                                  "activityLevel": "NORMAL",
                                  "goalType": "DIET"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    /** 닭가슴살 기본 요청 바디. date=null 이면 recordedDate 필드를 생략한다. */
    private String chickenBody(Long pId, String date) {
        return mealBody(pId, "닭가슴살", "LUNCH", 165.0, 31.0, 0.0, 3.6, date);
    }

    /** calories/proteinG/carbsG/fatG만 다를 때 사용하는 바디. */
    private String nutritionBody(Long pId, double cal, double protein, double carbs, double fat) {
        return mealBody(pId, "테스트식품", "LUNCH", cal, protein, carbs, fat, null);
    }

    private String mealBody(Long pId, String foodName, String mealType,
                             double cal, double protein, double carbs, double fat,
                             String date) {
        String dateField = (date != null)
                ? ", \"recordedDate\": \"%s\"".formatted(date)
                : "";
        return """
                {
                  "profileId": %d,
                  "foodName": "%s",
                  "mealType": "%s",
                  "calories": %s,
                  "proteinG": %s,
                  "carbsG": %s,
                  "fatG": %s
                  %s
                }
                """.formatted(pId, foodName, mealType, cal, protein, carbs, fat, dateField);
    }

    private long createMeal(Long pId, String foodName, String mealType,
                             double cal, double protein, double carbs, double fat,
                             String date) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mealBody(pId, foodName, mealType, cal, protein, carbs, fat, date)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
