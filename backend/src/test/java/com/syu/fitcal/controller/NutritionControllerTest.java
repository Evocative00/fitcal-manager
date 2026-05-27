package com.syu.fitcal.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NutritionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(strings = {"DIET", "MAINTAIN", "BULK_UP", "HIGH_PROTEIN"})
    void calculateReturnsFixedResponseFieldsForAllGoalTypes(String goalType) throws Exception {
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(goalType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalType").value(goalType))
                .andExpect(jsonPath("$.goalLabel").isString())
                .andExpect(jsonPath("$.bmr").isNumber())
                .andExpect(jsonPath("$.tdee").isNumber())
                .andExpect(jsonPath("$.targetCalories").isNumber())
                .andExpect(jsonPath("$.targetCarbs").isNumber())
                .andExpect(jsonPath("$.targetProtein").isNumber())
                .andExpect(jsonPath("$.targetFat").isNumber())
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void targetCaloriesFollowsGoalRule() throws Exception {
        JsonNode diet = calculateAndRead("DIET");
        JsonNode maintain = calculateAndRead("MAINTAIN");
        JsonNode bulkUp = calculateAndRead("BULK_UP");

        assertThat(diet.get("targetCalories").asDouble())
                .isLessThan(diet.get("tdee").asDouble());
        assertThat(maintain.get("targetCalories").asDouble())
                .isEqualTo(maintain.get("tdee").asDouble());
        assertThat(bulkUp.get("targetCalories").asDouble())
                .isGreaterThan(bulkUp.get("tdee").asDouble());
    }

    @Test
    void highProteinProvidesAtLeastTwoPointTwoGramsPerKilogram() throws Exception {
        JsonNode highProtein = calculateAndRead("HIGH_PROTEIN");

        assertThat(highProtein.get("targetProtein").asDouble())
                .isGreaterThanOrEqualTo(70.0 * 2.2);
    }

    @Test
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

    private JsonNode calculateAndRead(String goalType) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(goalType)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String requestBody(String goalType) {
        return """
                {
                  "age": 25,
                  "heightCm": 175.0,
                  "weightKg": 70.0,
                  "gender": "MALE",
                  "activityLevel": "NORMAL",
                  "goalType": "%s"
                }
                """.formatted(goalType);
    }
}
