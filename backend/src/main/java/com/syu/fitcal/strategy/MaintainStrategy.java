package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.GoalType;
import org.springframework.stereotype.Component;

/**
 * 체중 유지 전략: TDEE 그대로 유지.
 * 단백질 25% / 탄수화물 50% / 지방 25%
 */
@Component
public class MaintainStrategy implements NutritionStrategy {

    @Override
    public GoalType getGoalType() {
        return GoalType.MAINTAIN;
    }

    @Override
    public NutritionResult calculate(double tdee, double weightKg) {
        double targetCalories = tdee;

        double targetProtein = targetCalories * 0.25 / 4;
        double targetCarbs   = targetCalories * 0.50 / 4;
        double targetFat     = targetCalories * 0.25 / 9;

        return new NutritionResult(
                targetCalories, targetProtein, targetCarbs, targetFat,
                "현재 체중을 유지하는 균형 잡힌 식단 (단백질 25% / 탄수화물 50% / 지방 25%)"
        );
    }
}
