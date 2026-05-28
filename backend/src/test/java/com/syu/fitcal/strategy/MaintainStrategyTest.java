package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("MaintainStrategy 단위 테스트")
class MaintainStrategyTest {

    private MaintainStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new MaintainStrategy();
    }

    @Test
    @DisplayName("GoalType이 MAINTAIN을 반환한다")
    void goalTypeIsMaintain() {
        assertThat(strategy.getGoalType()).isEqualTo(GoalType.MAINTAIN);
    }

    @Test
    @DisplayName("목표 칼로리가 TDEE와 동일하다")
    void targetCaloriesEqualsTdee() {
        NutritionResult result = strategy.calculate(2000.0, 70.0, Gender.MALE);

        assertThat(result.targetCalories()).isEqualTo(2000.0);
    }

    @Test
    @DisplayName("영양소 비율: 단백질 25% / 탄수화물 50% / 지방 25%")
    void macroRatiosAreCorrect() {
        double tdee = 2000.0;
        NutritionResult result = strategy.calculate(tdee, 70.0, Gender.MALE);

        assertThat(result.targetProtein()).isCloseTo(125.0, within(0.01));   // 2000 * 0.25 / 4
        assertThat(result.targetCarbs()).isCloseTo(250.0, within(0.01));     // 2000 * 0.50 / 4
        assertThat(result.targetFat()).isCloseTo(55.56, within(0.01));       // 2000 * 0.25 / 9
    }

    @Test
    @DisplayName("칼로리 합이 TDEE에 근접한다 (영양소별 반올림 오차 허용)")
    void caloriesSumApproximatelyTdee() {
        double tdee = 2000.0;
        NutritionResult result = strategy.calculate(tdee, 70.0, Gender.FEMALE);

        double sumKcal = result.targetProtein() * 4
                + result.targetCarbs() * 4
                + result.targetFat() * 9;

        assertThat(sumKcal).isCloseTo(tdee, within(1.0));
    }
}
