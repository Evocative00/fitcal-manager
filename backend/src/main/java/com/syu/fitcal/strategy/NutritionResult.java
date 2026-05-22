package com.syu.fitcal.strategy;

/**
 * 영양 계산 전략의 출력값.
 * 칼로리(kcal) 및 각 영양소(g) 수치와 설명 메시지를 담는다.
 */
public record NutritionResult(
        double targetCalories,
        double targetProtein,
        double targetCarbs,
        double targetFat,
        String message
) {}
