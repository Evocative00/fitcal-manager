package com.syu.fitcal.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.fitcal.repository.MealRecordRepository;
import com.syu.fitcal.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MealRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MealRecordRepository mealRecordRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private Long profileId;

    @BeforeEach
    void setUp() throws Exception {
        mealRecordRepository.deleteAll();
        userProfileRepository.deleteAll();
        profileId = createProfileAndReturnId();
    }

    @Test
    @DisplayName("POST /api/meals: 식단 기록을 생성한다")
    void createMealRecordReturnsCreatedRecord() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validMealCreateRequest("현미밥과 닭가슴살", "BREAKFAST", "2026-05-27", 520, 38, 65, 12)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.profileId").value(profileId))
                .andExpect(jsonPath("$.foodName").value("현미밥과 닭가슴살"))
                .andExpect(jsonPath("$.mealType").value("BREAKFAST"))
                .andExpect(jsonPath("$.mealTypeLabel").value("아침"))
                .andExpect(jsonPath("$.calories").value(520.0))
                .andExpect(jsonPath("$.proteinG").value(38.0))
                .andExpect(jsonPath("$.carbsG").value(65.0))
                .andExpect(jsonPath("$.fatG").value(12.0))
                .andExpect(jsonPath("$.recordedDate").value("2026-05-27"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("GET /api/meals?profileId=&date=: 특정 날짜 식단 목록만 조회한다")
    void findMealRecordsReturnsRecordsFilteredByProfileAndDate() throws Exception {
        createMealAndReturnId("아침 식단", "BREAKFAST", "2026-05-27", 400, 30, 50, 10);
        createMealAndReturnId("점심 식단", "LUNCH", "2026-05-27", 650, 45, 80, 18);
        createMealAndReturnId("다른 날짜 식단", "DINNER", "2026-05-28", 700, 35, 90, 20);

        mockMvc.perform(get("/api/meals")
                        .param("profileId", String.valueOf(profileId))
                        .param("date", "2026-05-27"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].recordedDate").value("2026-05-27"))
                .andExpect(jsonPath("$[1].recordedDate").value("2026-05-27"));
    }

    @Test
    @DisplayName("GET /api/meals/{id}: 단건 식단 기록을 조회한다")
    void findMealRecordReturnsSavedRecord() throws Exception {
        long mealId = createMealAndReturnId("단건 조회 식단", "DINNER", "2026-05-27", 700, 40, 90, 21);

        mockMvc.perform(get("/api/meals/{id}", mealId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mealId))
                .andExpect(jsonPath("$.foodName").value("단건 조회 식단"));
    }

    @Test
    @DisplayName("PUT /api/meals/{id}: 식단 기록을 수정한다")
    void updateMealRecordReturnsUpdatedRecord() throws Exception {
        long mealId = createMealAndReturnId("수정 전 식단", "BREAKFAST", "2026-05-27", 520, 38, 65, 12);

        mockMvc.perform(put("/api/meals/{id}", mealId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validMealUpdateRequest("닭가슴살 샐러드", "LUNCH", "2026-05-28", 430, 45, 35, 14)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mealId))
                .andExpect(jsonPath("$.profileId").value(profileId))
                .andExpect(jsonPath("$.foodName").value("닭가슴살 샐러드"))
                .andExpect(jsonPath("$.mealType").value("LUNCH"))
                .andExpect(jsonPath("$.recordedDate").value("2026-05-28"))
                .andExpect(jsonPath("$.calories").value(430.0))
                .andExpect(jsonPath("$.proteinG").value(45.0))
                .andExpect(jsonPath("$.carbsG").value(35.0))
                .andExpect(jsonPath("$.fatG").value(14.0));
    }

    @Test
    @DisplayName("DELETE /api/meals/{id}: 식단 기록을 삭제한다")
    void deleteMealRecordReturnsNoContent() throws Exception {
        long mealId = createMealAndReturnId("삭제 대상 식단", "SNACK", "2026-05-27", 250, 12, 30, 8);

        mockMvc.perform(delete("/api/meals/{id}", mealId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/meals/{id}", mealId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/meals/summary: 특정 날짜 섭취량 합산과 mealCount를 반환한다")
    void summarizeMealRecordsReturnsDailyTotals() throws Exception {
        createMealAndReturnId("아침 식단", "BREAKFAST", "2026-05-27", 400, 30, 50, 10);
        createMealAndReturnId("점심 식단", "LUNCH", "2026-05-27", 650, 45, 80, 18);
        createMealAndReturnId("다른 날짜 식단", "DINNER", "2026-05-28", 700, 35, 90, 20);

        mockMvc.perform(get("/api/meals/summary")
                        .param("profileId", String.valueOf(profileId))
                        .param("date", "2026-05-27"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value(profileId))
                .andExpect(jsonPath("$.date").value("2026-05-27"))
                .andExpect(jsonPath("$.consumedCalories").value(1050.0))
                .andExpect(jsonPath("$.consumedCarbs").value(130.0))
                .andExpect(jsonPath("$.consumedProtein").value(75.0))
                .andExpect(jsonPath("$.consumedFat").value(28.0))
                .andExpect(jsonPath("$.mealCount").value(2));
    }

    @Test
    @DisplayName("GET /api/meals/summary: 식단이 없는 날짜는 0 합계와 0건을 반환한다")
    void summarizeMealRecordsReturnsZerosWhenNoMealsExist() throws Exception {
        mockMvc.perform(get("/api/meals/summary")
                        .param("profileId", String.valueOf(profileId))
                        .param("date", "2026-05-27"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consumedCalories").value(0.0))
                .andExpect(jsonPath("$.consumedCarbs").value(0.0))
                .andExpect(jsonPath("$.consumedProtein").value(0.0))
                .andExpect(jsonPath("$.consumedFat").value(0.0))
                .andExpect(jsonPath("$.mealCount").value(0));
    }

    @Test
    @DisplayName("잘못된 profileId로 식단을 생성하면 404를 반환한다")
    void createMealRecordReturnsNotFoundWhenProfileDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "profileId": 9999,
                                  "foodName": "없는 프로필 식단",
                                  "mealType": "BREAKFAST",
                                  "calories": 520,
                                  "proteinG": 38,
                                  "carbsG": 65,
                                  "fatG": 12,
                                  "recordedDate": "2026-05-27"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("프로필을 찾을 수 없습니다. id=9999"));
    }

    @Test
    @DisplayName("잘못된 mealType이면 400을 반환한다")
    void createMealRecordReturnsBadRequestWhenMealTypeIsInvalid() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "profileId": %d,
                                  "foodName": "잘못된 식사 유형",
                                  "mealType": "BRUNCH",
                                  "calories": 520,
                                  "proteinG": 38,
                                  "carbsG": 65,
                                  "fatG": 12,
                                  "recordedDate": "2026-05-27"
                                }
                                """.formatted(profileId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 JSON 형식 또는 enum 값이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("음수 영양값이면 400을 반환한다")
    void createMealRecordReturnsBadRequestWhenNutritionValueIsNegative() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "profileId": %d,
                                  "foodName": "음수 영양값",
                                  "mealType": "SNACK",
                                  "calories": 200,
                                  "proteinG": -1,
                                  "carbsG": 20,
                                  "fatG": 5,
                                  "recordedDate": "2026-05-27"
                                }
                                """.formatted(profileId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
    }

    private Long createProfileAndReturnId() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "고상준",
                                  "age": 24,
                                  "heightCm": 175.0,
                                  "weightKg": 70.0,
                                  "gender": "MALE",
                                  "activityLevel": "NORMAL",
                                  "goalType": "DIET"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("id").asLong();
    }

    private long createMealAndReturnId(
            String foodName,
            String mealType,
            String recordedDate,
            int calories,
            int proteinG,
            int carbsG,
            int fatG
    ) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validMealCreateRequest(foodName, mealType, recordedDate, calories, proteinG, carbsG, fatG)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("id").asLong();
    }

    private String validMealCreateRequest(
            String foodName,
            String mealType,
            String recordedDate,
            int calories,
            int proteinG,
            int carbsG,
            int fatG
    ) {
        return """
                {
                  "profileId": %d,
                  "foodName": "%s",
                  "mealType": "%s",
                  "calories": %d,
                  "proteinG": %d,
                  "carbsG": %d,
                  "fatG": %d,
                  "recordedDate": "%s"
                }
                """.formatted(profileId, foodName, mealType, calories, proteinG, carbsG, fatG, recordedDate);
    }

    private String validMealUpdateRequest(
            String foodName,
            String mealType,
            String recordedDate,
            int calories,
            int proteinG,
            int carbsG,
            int fatG
    ) {
        return """
                {
                  "profileId": %d,
                  "foodName": "%s",
                  "mealType": "%s",
                  "calories": %d,
                  "proteinG": %d,
                  "carbsG": %d,
                  "fatG": %d,
                  "recordedDate": "%s"
                }
                """.formatted(profileId, foodName, mealType, calories, proteinG, carbsG, fatG, recordedDate);
    }
}
