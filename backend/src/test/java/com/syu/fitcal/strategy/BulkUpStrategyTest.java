package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("BulkUpStrategy 단위 테스트")
class BulkUpStrategyTest {

    private BulkUpStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BulkUpStrategy();
    }

    @Test
    @DisplayName("GoalType이 BULK_UP을 반환한다")
    void goalTypeIsBulkUp() {
        assertThat(strategy.getGoalType()).isEqualTo(GoalType.BULK_UP);
    }

    @Test
    @DisplayName("목표 칼로리가 TDEE + 400이다")
    void targetCaloriesHas400Surplus() {
        NutritionResult result = strategy.calculate(2000.0, 70.0, Gender.MALE);

        assertThat(result.targetCalories()).isEqualTo(2400.0);
    }

    @Test
    @DisplayName("영양소 비율: 단백질 30% / 탄수화물 50% / 지방 20%")
    void macroRatiosAreCorrect() {
        double targetCal = 2400.0; // tdee 2000 + 400
        NutritionResult result = strategy.calculate(2000.0, 70.0, Gender.MALE);

        assertThat(result.targetProtein()).isCloseTo(targetCal * 0.30 / 4, within(0.01)); // 180.0
        assertThat(result.targetCarbs()).isCloseTo(targetCal * 0.50 / 4, within(0.01));   // 300.0
        assertThat(result.targetFat()).isCloseTo(targetCal * 0.20 / 9, within(0.01));     // 53.33
    }

    @Test
    @DisplayName("칼로리 합이 목표 칼로리에 근접한다")
    void caloriesSumApproximatelyTargetCalories() {
        NutritionResult result = strategy.calculate(2000.0, 70.0, Gender.FEMALE);

        double sumKcal = result.targetProtein() * 4
                + result.targetCarbs() * 4
                + result.targetFat() * 9;

        assertThat(sumKcal).isCloseTo(result.targetCalories(), within(1.0));
    }
}
