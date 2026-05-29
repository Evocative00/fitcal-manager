package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import org.springframework.stereotype.Component;

/**
 * 벌크업 전략 — 근육 합성을 위한 칼로리 잉여 식단.
 *
 * <h3>계산 기준</h3>
 * <ul>
 *   <li><b>목표 칼로리</b>: TDEE + 400 kcal (클린 벌크 권장 잉여량)</li>
 *   <li><b>영양소 비율</b>: 단백질 30% / 탄수화물 50% / 지방 20%</li>
 * </ul>
 *
 * <p>탄수화물 비중을 높여 근합성을 위한 인슐린 분비와 글리코겐 보충을 최적화합니다.</p>
 */
@Component
public class BulkUpStrategy implements NutritionStrategy {

    private static final double SURPLUS = 400.0;

    @Override
    public GoalType getGoalType() {
        return GoalType.BULK_UP;
    }

    @Override
    public NutritionResult calculate(double tdee, double weightKg, Gender gender) {
        double targetCalories = tdee + SURPLUS;

        double targetProtein = targetCalories * 0.30 / 4;
        double targetCarbs   = targetCalories * 0.50 / 4;
        double targetFat     = targetCalories * 0.20 / 9;

        return new NutritionResult(
                targetCalories, targetProtein, targetCarbs, targetFat,
                "하루 400kcal 잉여로 근육 합성 극대화 (단백질 30% / 탄수화물 50% / 지방 20%)"
        );
    }
}
