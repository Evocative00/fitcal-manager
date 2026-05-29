package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("DietStrategy 단위 테스트")
class DietStrategyTest {

    private DietStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new DietStrategy();
    }

    @Test
    @DisplayName("GoalType이 DIET를 반환한다")
    void goalTypeIsDiet() {
        assertThat(strategy.getGoalType()).isEqualTo(GoalType.DIET);
    }

    @Test
    @DisplayName("남성: TDEE - 500이 최솟값(1500)을 넘으면 적자 칼로리를 적용한다")
    void maleNormalDeficit() {
        NutritionResult result = strategy.calculate(2500.0, 70.0, Gender.MALE);

        assertThat(result.targetCalories()).isEqualTo(2000.0);
        assertThat(result.targetProtein()).isCloseTo(150.0, within(0.01));   // 2000 * 0.30 / 4
        assertThat(result.targetCarbs()).isCloseTo(200.0, within(0.01));     // 2000 * 0.40 / 4
        assertThat(result.targetFat()).isCloseTo(66.67, within(0.01));       // 2000 * 0.30 / 9
    }

    @Test
    @DisplayName("남성: TDEE - 500이 1500 미만이면 1500kcal 하한선을 적용한다")
    void maleFloorApplied() {
        // 1800 - 500 = 1300 < 1500 → floor = 1500
        NutritionResult result = strategy.calculate(1800.0, 60.0, Gender.MALE);

        assertThat(result.targetCalories()).isEqualTo(1500.0);
        assertThat(result.targetProtein()).isCloseTo(112.5, within(0.01));
        assertThat(result.targetCarbs()).isCloseTo(150.0, within(0.01));
        assertThat(result.targetFat()).isCloseTo(50.0, within(0.01));
    }

    @Test
    @DisplayName("여성: TDEE - 500이 1200 미만이면 1200kcal 하한선을 적용한다")
    void femaleFloorApplied() {
        // 1600 - 500 = 1100 < 1200 → floor = 1200
        NutritionResult result = strategy.calculate(1600.0, 50.0, Gender.FEMALE);

        assertThat(result.targetCalories()).isEqualTo(1200.0);
        assertThat(result.targetProtein()).isCloseTo(90.0, within(0.01));
        assertThat(result.targetCarbs()).isCloseTo(120.0, within(0.01));
        assertThat(result.targetFat()).isCloseTo(40.0, within(0.01));
    }

    @Test
    @DisplayName("여성: TDEE - 500이 1200 이상이면 하한선 없이 적자를 적용한다")
    void femaleNoFloor() {
        NutritionResult result = strategy.calculate(2500.0, 60.0, Gender.FEMALE);

        assertThat(result.targetCalories()).isEqualTo(2000.0);
    }

    @Test
    @DisplayName("하한선이 적용되면 메시지에 최소 칼로리 안내 문구가 포함된다")
    void messageIndicatesFloorWhenApplied() {
        NutritionResult result = strategy.calculate(1600.0, 50.0, Gender.FEMALE);

        assertThat(result.message()).contains("최소 권장 섭취 칼로리");
    }

    @Test
    @DisplayName("하한선이 적용되지 않으면 메시지에 500kcal 적자 안내 문구가 포함된다")
    void messageIndicatesDeficitWhenNoFloor() {
        NutritionResult result = strategy.calculate(2500.0, 70.0, Gender.MALE);

        assertThat(result.message()).contains("500kcal 적자");
    }
}
