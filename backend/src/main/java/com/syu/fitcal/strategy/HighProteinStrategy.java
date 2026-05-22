package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.GoalType;
import org.springframework.stereotype.Component;

/**
 * 고단백 전략: 체중 1kg당 2.2g 단백질 우선 확보.
 * 지방 25% 고정 후 나머지를 탄수화물로 충당.
 */
@Component
public class HighProteinStrategy implements NutritionStrategy {

    private static final double PROTEIN_PER_KG = 2.2;
    private static final double FAT_RATIO       = 0.25;
    private static final double MIN_CARBS_G     = 50.0;

    @Override
    public GoalType getGoalType() {
        return GoalType.HIGH_PROTEIN;
    }

    @Override
    public NutritionResult calculate(double tdee, double weightKg) {
        double targetCalories = tdee;
        double targetProtein  = weightKg * PROTEIN_PER_KG;
        double targetFat      = targetCalories * FAT_RATIO / 9;

        double remainingCalories = targetCalories - (targetProtein * 4) - (targetFat * 9);
        double targetCarbs = Math.max(remainingCalories / 4, MIN_CARBS_G);

        return new NutritionResult(
                targetCalories, targetProtein, targetCarbs, targetFat,
                String.format("체중 1kg당 단백질 %.1fg (총 %.0fg) 확보 — 근손실 방지 및 포만감 유지",
                        PROTEIN_PER_KG, targetProtein)
        );
    }
}
