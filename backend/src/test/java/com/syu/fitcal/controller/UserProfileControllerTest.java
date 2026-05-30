package com.syu.fitcal.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syu.fitcal.repository.MealRecordRepository;
import com.syu.fitcal.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MealRecordRepository mealRecordRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @BeforeEach
    void setUp() {
        // MealRecord → UserProfile FK 제약으로 인해 자식 테이블을 먼저 삭제
        mealRecordRepository.deleteAll();
        userProfileRepository.deleteAll();
    }

    @Test
    void createProfileReturnsCreatedProfile() throws Exception {
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("고상준"))
                .andExpect(jsonPath("$.age").value(24))
                .andExpect(jsonPath("$.heightCm").value(175.0))
                .andExpect(jsonPath("$.weightKg").value(70.0))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.activityLevel").value("NORMAL"))
                .andExpect(jsonPath("$.goalType").value("DIET"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void findProfilesReturnsSavedProfiles() throws Exception {
        createProfileAndReturnId();

        mockMvc.perform(get("/api/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("고상준"))
                .andExpect(jsonPath("$[0].goalType").value("DIET"));
    }

    @Test
    void findProfileReturnsSavedProfile() throws Exception {
        long profileId = createProfileAndReturnId();

        mockMvc.perform(get("/api/profiles/{id}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId))
                .andExpect(jsonPath("$.name").value("고상준"));
    }

    @Test
    void findProfileReturnsNotFoundWhenProfileDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/profiles/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("프로필을 찾을 수 없습니다. id=9999"));
    }

    @Test
    void createProfileReturnsBadRequestWhenRequiredValueIsMissing() throws Exception {
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "age": 24,
                                  "heightCm": 175.0,
                                  "weightKg": 70.0,
                                  "gender": "MALE",
                                  "activityLevel": "NORMAL",
                                  "goalType": "DIET"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
    }

    private long createProfileAndReturnId() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest()))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("id").asLong();
    }

    private String validCreateRequest() {
        return """
                {
                  "name": "고상준",
                  "age": 24,
                  "heightCm": 175.0,
                  "weightKg": 70.0,
                  "gender": "MALE",
                  "activityLevel": "NORMAL",
                  "goalType": "DIET"
                }
                """;
    }
}
