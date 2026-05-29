package com.syu.fitcal.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * POST /api/nutrition/calculate 통합 테스트.
 *
 * <h3>기준 입력값 (공통)</h3>
 * <pre>
 *   weightKg=70.0, heightCm=175.0, age=25, gender=MALE, activityLevel=NORMAL(×1.55)
 *   BMR  = 66.5 + 13.75×70 + 5.003×175 − 6.75×25 = 1735.775 → 1735.8
 *   TDEE = 1735.775 × 1.55 = 2690.45125             → 2690.5
 * </pre>
 */
@SpringBootTest
@AutoConfigureMockMvc
class NutritionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ───────────── 공통 기준 입력 ─────────────

    /** BMR=1735.8, TDEE=2690.5 */
    private String baseRequest(String goalType) {
        return """
                {
                  "weightKg": 70.0,
                  "heightCm": 175.0,
                  "age": 25,
                  "gender": "MALE",
                  "activityLevel": "NORMAL",
                  "goalType": "%s"
                }
                """.formatted(goalType);
    }

    // ───────────── 모든 목표 유형 정상 응답 ─────────────

    @ParameterizedTest
    @ValueSource(strings = {"DIET", "MAINTAIN", "BULK_UP", "HIGH_PROTEIN"})
    @DisplayName("모든 목표 유형에 대해 200 OK와 필수 필드를 반환한다")
    void calculateReturnsOkForAllGoalTypes(String goalType) throws Exception {
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(baseRequest(goalType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalType").value(goalType))
                .andExpect(jsonPath("$.goalLabel").isString())
                .andExpect(jsonPath("$.bmr").isNumber())
                .andExpect(jsonPath("$.tdee").isNumber())
                .andExpect(jsonPath("$.targetCalories").isNumber())
                .andExpect(jsonPath("$.targetProtein").isNumber())
                .andExpect(jsonPath("$.targetCarbs").isNumber())
                .andExpect(jsonPath("$.targetFat").isNumber())
                .andExpect(jsonPath("$.message").isString());
    }

    // ───────────── 목표별 실제 계산값 검증 ─────────────

    @Test
    @DisplayName("DIET: targetCalories = TDEE(2690.5) − 500 = 2190.5, 영양소 비율 30/40/30")
    void dietCalculationValues() throws Exception {
        // targetCalories = 2190.5
        // targetProtein  = 2190.5 × 0.30 / 4 = 164.3
        // targetCarbs    = 2190.5 × 0.40 / 4 = 219.1
        // targetFat      = 2190.5 × 0.30 / 9 = 73.0
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(baseRequest("DIET")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmr").value(1735.8))
                .andExpect(jsonPath("$.tdee").value(2690.5))
                .andExpect(jsonPath("$.targetCalories").value(2190.5))
                .andExpect(jsonPath("$.targetProtein").value(164.3))
                .andExpect(jsonPath("$.targetCarbs").value(219.1))
                .andExpect(jsonPath("$.targetFat").value(73.0));
    }

    @Test
    @DisplayName("MAINTAIN: targetCalories = TDEE(2690.5), 영양소 비율 25/50/25")
    void maintainCalculationValues() throws Exception {
        // targetProtein  = 2690.5 × 0.25 / 4 = 168.2
        // targetCarbs    = 2690.5 × 0.50 / 4 = 336.3
        // targetFat      = 2690.5 × 0.25 / 9 = 74.7
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(baseRequest("MAINTAIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetCalories").value(2690.5))
                .andExpect(jsonPath("$.targetProtein").value(168.2))
                .andExpect(jsonPath("$.targetCarbs").value(336.3))
                .andExpect(jsonPath("$.targetFat").value(74.7));
    }

    @Test
    @DisplayName("BULK_UP: targetCalories = TDEE(2690.5) + 400 = 3090.5, 영양소 비율 30/50/20")
    void bulkUpCalculationValues() throws Exception {
        // targetProtein  = 3090.5 × 0.30 / 4 = 231.8
        // targetCarbs    = 3090.5 × 0.50 / 4 = 386.3
        // targetFat      = 3090.5 × 0.20 / 9 = 68.7
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(baseRequest("BULK_UP")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetCalories").value(3090.5))
                .andExpect(jsonPath("$.targetProtein").value(231.8))
                .andExpect(jsonPath("$.targetCarbs").value(386.3))
                .andExpect(jsonPath("$.targetFat").value(68.7));
    }

    @Test
    @DisplayName("HIGH_PROTEIN: 단백질 = 체중(70) × 2.2 = 154.0g, 나머지 TDEE에서 배분")
    void highProteinCalculationValues() throws Exception {
        // targetProtein  = 70 × 2.2 = 154.0
        // targetFat      = 2690.5 × 0.25 / 9 = 74.7
        // remaining      = 2690.5 − 154×4 − 74.7361×9 = 1401.875 → targetCarbs = 350.5
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(baseRequest("HIGH_PROTEIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetCalories").value(2690.5))
                .andExpect(jsonPath("$.targetProtein").value(154.0))
                .andExpect(jsonPath("$.targetCarbs").value(350.5))
                .andExpect(jsonPath("$.targetFat").value(74.7));
    }

    // ───────────── 최소 칼로리 하한선 ─────────────

    @Test
    @DisplayName("DIET 여성: TDEE − 500이 1200 미만이면 1200kcal 하한선이 적용된다")
    void dietFemaleMinimumFloorApplied() throws Exception {
        // BMR  = 655.1 + 9.563×50 + 1.850×155 − 4.676×30 = 1279.72 → 1279.7
        // TDEE = 1279.72 × 1.2 = 1535.664 → 1535.7
        // raw  = 1535.7 − 500 = 1035.7 < 1200 → floor = 1200.0
        String request = """
                {
                  "weightKg": 50.0,
                  "heightCm": 155.0,
                  "age": 30,
                  "gender": "FEMALE",
                  "activityLevel": "LOW",
                  "goalType": "DIET"
                }
                """;
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetCalories").value(1200.0))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("최소 권장 섭취 칼로리")));
    }

    // ───────────── 오류 케이스 ─────────────

    @Test
    @DisplayName("필수 필드 누락 시 400을 반환한다")
    void calculateReturnsBadRequestWhenRequiredFieldIsMissing() throws Exception {
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "weightKg": 70.0,
                                  "heightCm": 175.0,
                                  "age": 25,
                                  "gender": "MALE",
                                  "activityLevel": "NORMAL"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("잘못된 enum 값 전달 시 400과 오류 메시지를 반환한다")
    void calculateReturnsBadRequestOnInvalidEnum() throws Exception {
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "weightKg": 70.0,
                                  "heightCm": 175.0,
                                  "age": 25,
                                  "gender": "MALE",
                                  "activityLevel": "NORMAL",
                                  "goalType": "INVALID_GOAL"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 JSON 형식 또는 enum 값이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("범위를 벗어난 체중 입력 시 400과 상세 오류를 반환한다")
    void calculateReturnsBadRequestWhenWeightOutOfRange() throws Exception {
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "weightKg": 10.0,
                                  "heightCm": 175.0,
                                  "age": 25,
                                  "gender": "MALE",
                                  "activityLevel": "NORMAL",
                                  "goalType": "DIET"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value(
                        org.hamcrest.Matchers.containsString("weightKg")));
    }
}
