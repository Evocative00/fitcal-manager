package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.GoalType;
import org.springframework.stereotype.Component;

/**
 * 다이어트 전략: TDEE에서 500kcal 적자 적용.
 * 단백질 30% / 탄수화물 40% / 지방 30%
 */
@Component
public class DietStrategy implements NutritionStrategy {

    @Override
    public GoalType getGoalType() {
        return GoalType.DIET;
    }

    @Override
    public NutritionResult calculate(double tdee, double weightKg) {
        double targetCalories = tdee - 500;

        double targetProtein = targetCalories * 0.30 / 4;
        double targetCarbs   = targetCalories * 0.40 / 4;
        double targetFat     = targetCalories * 0.30 / 9;

        return new NutritionResult(
                targetCalories, targetProtein, targetCarbs, targetFat,
                "하루 500kcal 적자로 주 0.5kg 체지방 감량 목표 (단백질 30% / 탄수화물 40% / 지방 30%)"
        );
    }
}
