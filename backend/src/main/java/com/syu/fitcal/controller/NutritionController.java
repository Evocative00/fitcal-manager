package com.syu.fitcal.controller;

import com.syu.fitcal.dto.NutritionRequest;
import com.syu.fitcal.dto.NutritionResponse;
import com.syu.fitcal.service.NutritionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    private final NutritionService nutritionService;

    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    /**
     * POST /api/nutrition/calculate
     * 신체 정보와 목표를 기반으로 하루 영양 권장량을 계산합니다.
     */
    @PostMapping("/calculate")
    public ResponseEntity<NutritionResponse> calculate(@Valid @RequestBody NutritionRequest request) {
        return ResponseEntity.ok(nutritionService.calculate(request));
    }
}
