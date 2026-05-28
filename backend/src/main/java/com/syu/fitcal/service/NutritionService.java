package com.syu.fitcal.service;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import com.syu.fitcal.dto.NutritionRequest;
import com.syu.fitcal.dto.NutritionResponse;
import com.syu.fitcal.exception.UnsupportedGoalTypeException;
import com.syu.fitcal.strategy.NutritionResult;
import com.syu.fitcal.strategy.NutritionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 영양 계산 서비스.
 *
 * <p>BMR과 TDEE를 계산한 뒤, 요청된 {@link GoalType}에 맞는
 * {@link NutritionStrategy} 구현체에 위임해 영양 권장량을 산출합니다.</p>
 *
 * <h3>계산 흐름</h3>
 * <ol>
 *   <li>Harris-Benedict 개정 공식으로 BMR 계산</li>
 *   <li>BMR × 활동계수({@link com.syu.fitcal.domain.ActivityLevel#getMultiplier()})로 TDEE 계산</li>
 *   <li>{@link GoalType} → {@link NutritionStrategy} 매핑으로 전략 선택</li>
 *   <li>전략의 {@code calculate(tdee, weightKg, gender)} 호출</li>
 *   <li>소수점 첫째 자리로 반올림한 결과를 {@link NutritionResponse}로 반환</li>
 * </ol>
 */
@Service
public class NutritionService {

    private static final Logger log = LoggerFactory.getLogger(NutritionService.class);

    private final Map<GoalType, NutritionStrategy> strategyMap;

    /**
     * {@link NutritionStrategy} 빈 목록을 주입받아 {@link GoalType} 기준으로 매핑합니다.
     * 새 전략 빈을 추가하면 자동으로 등록됩니다.
     */
    public NutritionService(List<NutritionStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NutritionStrategy::getGoalType, s -> s));
        log.info("등록된 영양 계산 전략: {}", strategyMap.keySet());
    }

    /**
     * 사용자 요청으로부터 하루 영양 권장량을 계산합니다.
     *
     * @param request 신체 정보 및 목표 정보
     * @return        BMR, TDEE, 영양소별 권장량이 담긴 응답
     * @throws UnsupportedGoalTypeException 요청한 GoalType에 대한 전략이 등록되지 않은 경우
     */
    public NutritionResponse calculate(NutritionRequest request) {
        double bmr  = calculateBmr(request);
        double tdee = round(bmr * request.activityLevel().getMultiplier());

        NutritionStrategy strategy = strategyMap.get(request.goalType());
        if (strategy == null) {
            throw new UnsupportedGoalTypeException(request.goalType());
        }

        NutritionResult result = strategy.calculate(tdee, request.weightKg(), request.gender());

        return new NutritionResponse(
                request.goalType(),
                request.goalType().getLabel(),
                round(bmr),
                round(tdee),
                round(result.targetCalories()),
                round(result.targetProtein()),
                round(result.targetCarbs()),
                round(result.targetFat()),
                result.message()
        );
    }

    /**
     * Harris-Benedict 개정 공식으로 기초대사량(BMR)을 계산합니다.
     *
     * <pre>
     *   남성: 66.5 + (13.75 × kg) + (5.003 × cm) − (6.75 × age)
     *   여성: 655.1 + (9.563 × kg) + (1.850 × cm) − (4.676 × age)
     * </pre>
     */
    private double calculateBmr(NutritionRequest req) {
        double w = req.weightKg();
        double h = req.heightCm();
        int    a = req.age();

        if (req.gender() == Gender.MALE) {
            return 66.5 + (13.75 * w) + (5.003 * h) - (6.75 * a);
        } else {
            return 655.1 + (9.563 * w) + (1.850 * h) - (4.676 * a);
        }
    }

    /** 소수점 첫째 자리로 반올림합니다. */
    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
