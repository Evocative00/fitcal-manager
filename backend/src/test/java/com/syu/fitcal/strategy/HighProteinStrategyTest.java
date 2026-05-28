package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("HighProteinStrategy 단위 테스트")
class HighProteinStrategyTest {

    private HighProteinStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new HighProteinStrategy();
    }

    @Test
    @DisplayName("GoalType이 HIGH_PROTEIN을 반환한다")
    void goalTypeIsHighProtein() {
        assertThat(strategy.getGoalType()).isEqualTo(GoalType.HIGH_PROTEIN);
    }

    @Test
    @DisplayName("목표 칼로리가 TDEE와 동일하다")
    void targetCaloriesEqualsTdee() {
        NutritionResult result = strategy.calculate(2000.0, 70.0, Gender.MALE);

        assertThat(result.targetCalories()).isEqualTo(2000.0);
    }

    @Test
    @DisplayName("단백질이 체중 × 2.2g으로 계산된다")
    void proteinBasedOnBodyWeight() {
        NutritionResult result = strategy.calculate(2000.0, 70.0, Gender.MALE);

        // 70kg × 2.2 = 154.0g
        assertThat(result.targetProtein()).isCloseTo(154.0, within(0.01));
    }

    @Test
    @DisplayName("잔여 칼로리가 음수가 될 만큼 단백질이 높아도 탄수화물은 최소 50g을 보장한다")
    void carbsHaveMinimumFloor_whenProteinIsVeryHigh() {
        // weightKg=400 → protein=880g → protein cal=3520 > tdee=2000
        NutritionResult result = strategy.calculate(2000.0, 400.0, Gender.MALE);

        assertThat(result.targetCarbs()).isGreaterThanOrEqualTo(50.0);
    }

    @Test
    @DisplayName("지방이 TDEE의 25%로 계산된다")
    void fatIsQuarterOfTdee() {
        double tdee = 2000.0;
        NutritionResult result = strategy.calculate(tdee, 70.0, Gender.FEMALE);

        // 2000 * 0.25 / 9 ≈ 55.56
        assertThat(result.targetFat()).isCloseTo(tdee * 0.25 / 9, within(0.01));
    }

    @Test
    @DisplayName("메시지에 체중당 단백질 섭취량이 포함된다")
    void messageContainsProteinPerKg() {
        NutritionResult result = strategy.calculate(2000.0, 70.0, Gender.MALE);

        assertThat(result.message()).contains("2.2");
    }
}
