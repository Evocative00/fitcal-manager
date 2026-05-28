package com.syu.fitcal.dto;

import com.syu.fitcal.domain.MealRecord;
import com.syu.fitcal.domain.MealType;
import com.syu.fitcal.domain.UserProfile;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MealRecordCreateRequest(
        @NotNull(message = "프로필 ID는 필수입니다.")
        Long profileId,

        @NotBlank(message = "음식명은 필수입니다.")
        @Size(max = 100, message = "음식명은 100자 이하여야 합니다.")
        String foodName,

        @NotNull(message = "식사 유형은 필수입니다.")
        MealType mealType,

        @NotNull(message = "칼로리는 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "칼로리는 0보다 커야 합니다.")
        Double calories,

        @NotNull(message = "단백질(g)은 필수입니다.")
        @DecimalMin(value = "0.0", message = "단백질은 0 이상이어야 합니다.")
        Double proteinG,

        @NotNull(message = "탄수화물(g)은 필수입니다.")
        @DecimalMin(value = "0.0", message = "탄수화물은 0 이상이어야 합니다.")
        Double carbsG,

        @NotNull(message = "지방(g)은 필수입니다.")
        @DecimalMin(value = "0.0", message = "지방은 0 이상이어야 합니다.")
        Double fatG,

        /** null이면 서비스에서 오늘 날짜로 처리됩니다. */
        LocalDate recordedDate
) {
    public MealRecord toEntity(UserProfile userProfile) {
        LocalDate date = (recordedDate != null) ? recordedDate : LocalDate.now();
        return new MealRecord(userProfile, foodName.trim(), mealType, calories, proteinG, carbsG, fatG, date);
    }
}
