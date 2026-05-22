package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.GoalType;
import org.springframework.stereotype.Component;

/**
 * 벌크업 전략: TDEE에 400kcal 잉여 적용.
 * 단백질 30% / 탄수화물 50% / 지방 20%
 */
@Component
public class BulkUpStrategy implements NutritionStrategy {

    @Override
    public GoalType getGoalType() {
        return GoalType.BULK_UP;
    }

    @Override
    public NutritionResult calculate(double tdee, double weightKg) {
        double targetCalories = tdee + 400;

        double targetProtein = targetCalories * 0.30 / 4;
        double targetCarbs   = targetCalories * 0.50 / 4;
        double targetFat     = targetCalories * 0.20 / 9;

        return new NutritionResult(
                targetCalories, targetProtein, targetCarbs, targetFat,
                "하루 400kcal 잉여로 근육 합성 극대화 (단백질 30% / 탄수화물 50% / 지방 20%)"
        );
    }
}
