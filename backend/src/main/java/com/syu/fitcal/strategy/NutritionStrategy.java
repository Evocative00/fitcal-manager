package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.GoalType;

public interface NutritionStrategy {

    GoalType getGoalType();

    /**
     * @param tdee      총 일일 에너지 소비량 (kcal)
     * @param weightKg  체중 (kg) — 고단백 전략의 단백질 계산에 활용
     */
    NutritionResult calculate(double tdee, double weightKg);
}
