package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import org.springframework.stereotype.Component;

/**
 * 체중 유지 전략 — TDEE를 그대로 목표 칼로리로 사용하는 균형 식단.
 *
 * <h3>계산 기준</h3>
 * <ul>
 *   <li><b>목표 칼로리</b>: TDEE (증감 없음)</li>
 *   <li><b>영양소 비율</b>: 단백질 25% / 탄수화물 50% / 지방 25%</li>
 * </ul>
 */
@Component
public class MaintainStrategy implements NutritionStrategy {

    @Override
    public GoalType getGoalType() {
        return GoalType.MAINTAIN;
    }

    @Override
    public NutritionResult calculate(double tdee, double weightKg, Gender gender) {
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
