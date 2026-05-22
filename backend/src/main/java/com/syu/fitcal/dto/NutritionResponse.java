package com.syu.fitcal.dto;

import com.syu.fitcal.domain.GoalType;

public record NutritionResponse(
        GoalType goalType,
        String goalLabel,
        double bmr,
        double tdee,
        double targetCalories,
        double targetProtein,
        double targetCarbs,
        double targetFat,
        String message
) {}
