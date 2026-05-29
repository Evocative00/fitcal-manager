package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import org.springframework.stereotype.Component;

/**
 * 다이어트 전략 — 체지방 감량을 위한 칼로리 적자 식단.
 *
 * <h3>계산 기준</h3>
 * <ul>
 *   <li><b>목표 칼로리</b>: TDEE − 500 kcal (주 0.5 kg 감량 속도)</li>
 *   <li><b>최소 하한선</b>: 남성 1,500 kcal / 여성 1,200 kcal (WHO 권고 기준)</li>
 *   <li><b>영양소 비율</b>: 단백질 30% / 탄수화물 40% / 지방 30%</li>
 * </ul>
 *
 * <h3>하한선 적용 이유</h3>
 * TDEE가 낮은 사용자(예: 소식 + 비활동)의 경우 적자 적용 후 칼로리가
 * 의학적 최소치 이하로 떨어질 수 있으므로, 하한선으로 클램프합니다.
 */
@Component
public class DietStrategy implements NutritionStrategy {

    private static final double DEFICIT             = 500.0;
    private static final double MIN_MALE_CALORIES   = 1500.0;
    private static final double MIN_FEMALE_CALORIES = 1200.0;

    @Override
    public GoalType getGoalType() {
        return GoalType.DIET;
    }

    @Override
    public NutritionResult calculate(double tdee, double weightKg, Gender gender) {
        double minimum       = (gender == Gender.MALE) ? MIN_MALE_CALORIES : MIN_FEMALE_CALORIES;
        double raw           = tdee - DEFICIT;
        double targetCalories = Math.max(raw, minimum);

        double targetProtein = targetCalories * 0.30 / 4;
        double targetCarbs   = targetCalories * 0.40 / 4;
        double targetFat     = targetCalories * 0.30 / 9;

        String message = (targetCalories > raw)
                ? String.format("최소 권장 섭취 칼로리(%.0fkcal)가 적용되었습니다. (단백질 30%% / 탄수화물 40%% / 지방 30%%)", minimum)
                : "하루 500kcal 적자로 주 0.5kg 체지방 감량 목표 (단백질 30% / 탄수화물 40% / 지방 30%)";

        return new NutritionResult(targetCalories, targetProtein, targetCarbs, targetFat, message);
    }
}
