package com.syu.fitcal.dto;

import com.syu.fitcal.domain.MealRecord;
import com.syu.fitcal.domain.MealType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MealRecordResponse(
        Long id,
        Long profileId,
        String foodName,
        MealType mealType,
        String mealTypeLabel,
        Double calories,
        Double proteinG,
        Double carbsG,
        Double fatG,
        LocalDate recordedDate,
        LocalDateTime createdAt
) {
    public static MealRecordResponse from(MealRecord record) {
        return new MealRecordResponse(
                record.getId(),
                record.getUserProfile().getId(),
                record.getFoodName(),
                record.getMealType(),
                record.getMealType().getLabel(),
                record.getCalories(),
                record.getProteinG(),
                record.getCarbsG(),
                record.getFatG(),
                record.getRecordedDate(),
                record.getCreatedAt()
        );
    }
}
