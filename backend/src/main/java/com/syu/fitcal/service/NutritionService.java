package com.syu.fitcal.service;

import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import com.syu.fitcal.dto.NutritionRequest;
import com.syu.fitcal.dto.NutritionResponse;
import com.syu.fitcal.strategy.NutritionResult;
import com.syu.fitcal.strategy.NutritionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NutritionService {

    private static final Logger log = LoggerFactory.getLogger(NutritionService.class);

    /** GoalType → 전략 빈 자동 매핑 */
    private final Map<GoalType, NutritionStrategy> strategyMap;

    public NutritionService(List<NutritionStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NutritionStrategy::getGoalType, s -> s));
        log.info("등록된 영양 계산 전략: {}", strategyMap.keySet());
    }

    public NutritionResponse calculate(NutritionRequest request) {
        double bmr  = calculateBmr(request);
        double tdee = round(bmr * request.activityLevel().getMultiplier());

        NutritionStrategy strategy = strategyMap.get(request.goalType());
        NutritionResult   result   = strategy.calculate(tdee, request.weightKg());

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
     * Harris-Benedict 개정 공식으로 BMR 계산.
     * 남성: 66.5 + (13.75 × kg) + (5.003 × cm) − (6.75 × age)
     * 여성: 655.1 + (9.563 × kg) + (1.850 × cm) − (4.676 × age)
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

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
