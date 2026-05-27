package com.syu.fitcal.dto;

import com.syu.fitcal.domain.GoalType;

public record NutritionResponse(
        GoalType goalType,
        String goalLabel,
        double bmr,
        double tdee,
        double targetCalories,
        double targetCarbs,
        double targetProtein,
        double targetFat,
        String message
) {}
