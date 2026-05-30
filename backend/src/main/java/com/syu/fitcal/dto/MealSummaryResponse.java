package com.syu.fitcal.dto;

import java.time.LocalDate;

/**
 * 특정 날짜의 식사 기록 합산 응답.
 *
 * <p>해당 날짜에 기록된 모든 MealRecord의 영양소를 합산합니다.
 * 기록이 없으면 모든 수치가 0.0으로 반환됩니다.</p>
 */
public record MealSummaryResponse(
        Long profileId,
        LocalDate date,
        double consumedCalories,
        double consumedCarbs,
        double consumedProtein,
        double consumedFat,
        int mealCount
) {}
