package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import org.springframework.stereotype.Component;

/**
 * 고단백 전략 — 체중 기반 단백질을 우선 확보하는 식단.
 *
 * <h3>계산 기준</h3>
 * <ul>
 *   <li><b>목표 칼로리</b>: TDEE (유지)</li>
 *   <li><b>단백질</b>: 체중(kg) × 2.2 g (근손실 방지 최소 권장치의 상단)</li>
 *   <li><b>지방</b>: TDEE × 25% ÷ 9 g</li>
 *   <li><b>탄수화물</b>: (TDEE − 단백질 칼로리 − 지방 칼로리) ÷ 4 g
 *       (최소 {@value #MIN_CARBS_G}g 보장)</li>
 * </ul>
 *
 * <h3>최소 탄수화물 보장 이유</h3>
 * 단백질이 매우 높으면 잔여 칼로리가 탄수화물로 배분될 때 음수가 될 수 있습니다.
 * 뇌·적혈구의 최소 포도당 요구량을 충족하기 위해 {@value #MIN_CARBS_G}g을 하한으로 설정합니다.
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
    public NutritionResult calculate(double tdee, double weightKg, Gender gender) {
        double targetCalories = tdee;
        double targetProtein  = weightKg * PROTEIN_PER_KG;
        double targetFat      = targetCalories * FAT_RATIO / 9;

        double remaining   = targetCalories - (targetProtein * 4) - (targetFat * 9);
        double targetCarbs = Math.max(remaining / 4, MIN_CARBS_G);

        return new NutritionResult(
                targetCalories, targetProtein, targetCarbs, targetFat,
                String.format("체중 1kg당 단백질 %.1fg (총 %.0fg) 확보 — 근손실 방지 및 포만감 유지",
                        PROTEIN_PER_KG, targetProtein)
        );
    }
}
