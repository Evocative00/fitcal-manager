package com.syu.fitcal.strategy;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;

/**
 * 목표별 영양 계산 전략 인터페이스.
 *
 * <p>구현체는 {@link org.springframework.stereotype.Component @Component}로 등록하면
 * {@code NutritionService}가 {@link GoalType}을 키로 자동 매핑합니다.
 * 새로운 목표가 생기면 이 인터페이스를 구현하는 빈만 추가하면 됩니다.</p>
 *
 * <h3>영양소 칼로리 환산 기준</h3>
 * <ul>
 *   <li>단백질 (Protein): 4 kcal/g</li>
 *   <li>탄수화물 (Carbohydrate): 4 kcal/g</li>
 *   <li>지방 (Fat): 9 kcal/g</li>
 * </ul>
 *
 * <h3>BMR 계산 — Harris-Benedict 개정 공식</h3>
 * <pre>
 *   남성: 66.5 + (13.75 × 체중kg) + (5.003 × 키cm) − (6.75 × 나이)
 *   여성: 655.1 + (9.563 × 체중kg) + (1.850 × 키cm) − (4.676 × 나이)
 * </pre>
 *
 * <h3>TDEE 계산</h3>
 * <pre>TDEE = BMR × 활동계수</pre>
 * <table border="1">
 *   <tr><th>활동 수준</th><th>계수</th></tr>
 *   <tr><td>LOW</td><td>1.2</td></tr>
 *   <tr><td>NORMAL</td><td>1.55</td></tr>
 *   <tr><td>HIGH</td><td>1.725</td></tr>
 *   <tr><td>VERY_HIGH</td><td>1.9</td></tr>
 * </table>
 */
public interface NutritionStrategy {

    /**
     * 이 전략이 처리하는 목표 유형을 반환합니다.
     *
     * @return 매핑 대상 {@link GoalType}
     */
    GoalType getGoalType();

    /**
     * 목표에 맞는 하루 영양 권장량을 계산합니다.
     *
     * @param tdee      총 일일 에너지 소비량 (kcal) — BMR × 활동계수
     * @param weightKg  체중 (kg) — 체중 기반 단백질 계산에 활용 (고단백 전략)
     * @param gender    성별 — 최소 칼로리 하한선 결정에 활용 (다이어트 전략)
     * @return          영양소별 권장량과 설명이 담긴 {@link NutritionResult}
     */
    NutritionResult calculate(double tdee, double weightKg, Gender gender);
}
