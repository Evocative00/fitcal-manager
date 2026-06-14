package com.syu.fitcal.dto;

import java.time.LocalDate;

public record MealSummaryResponse(
        Long profileId,
        LocalDate date,
        Double consumedCalories,
        Double consumedCarbs,
        Double consumedProtein,
        Double consumedFat,
        Long mealCount
) {
}
