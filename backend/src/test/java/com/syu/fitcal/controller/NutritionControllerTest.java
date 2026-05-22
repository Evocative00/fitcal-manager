package com.syu.fitcal.controller;

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

@SpringBootTest
@AutoConfigureMockMvc
class NutritionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"DIET", "MAINTAIN", "BULK_UP", "HIGH_PROTEIN"})
    void calculateReturnsOkForAllGoalTypes(String goalType) throws Exception {
        mockMvc.perform(post("/api/nutrition/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(goalType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalType").value(goalType))
                .andExpect(jsonPath("$.bmr").isNumber())
                .andExpect(jsonPath("$.tdee").isNumber())
                .andExpect(jsonPath("$.targetCalories").isNumber())
                .andExpect(jsonPath("$.targetProtein").isNumber())
                .andExpect(jsonPath("$.targetCarbs").isNumber())
                .andExpect(jsonPath("$.targetFat").isNumber())
                .andExpect(jsonPath("$.message").isString());
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
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isBadRequest());
    }

    private String requestBody(String goalType) {
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
}
