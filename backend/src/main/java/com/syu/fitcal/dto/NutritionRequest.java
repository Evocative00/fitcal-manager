package com.syu.fitcal.dto;

import com.syu.fitcal.domain.ActivityLevel;
import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record NutritionRequest(
        @NotNull(message = "몸무게는 필수입니다.")
        @DecimalMin(value = "20.0", message = "몸무게는 20kg 이상이어야 합니다.")
        @DecimalMax(value = "300.0", message = "몸무게는 300kg 이하여야 합니다.")
        Double weightKg,

        @NotNull(message = "키는 필수입니다.")
        @DecimalMin(value = "50.0", message = "키는 50cm 이상이어야 합니다.")
        @DecimalMax(value = "250.0", message = "키는 250cm 이하여야 합니다.")
        Double heightCm,

        @NotNull(message = "나이는 필수입니다.")
        @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
        @Max(value = 120, message = "나이는 120 이하여야 합니다.")
        Integer age,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @NotNull(message = "활동량은 필수입니다.")
        ActivityLevel activityLevel,

        @NotNull(message = "목표는 필수입니다.")
        GoalType goalType
) {}
