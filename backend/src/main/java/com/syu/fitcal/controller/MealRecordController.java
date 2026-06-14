package com.syu.fitcal.controller;

import com.syu.fitcal.dto.MealRecordCreateRequest;
import com.syu.fitcal.dto.MealRecordResponse;
import com.syu.fitcal.dto.MealRecordUpdateRequest;
import com.syu.fitcal.dto.MealSummaryResponse;
import com.syu.fitcal.service.MealRecordService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealRecordController {

    private final MealRecordService mealRecordService;

    public MealRecordController(MealRecordService mealRecordService) {
        this.mealRecordService = mealRecordService;
    }

    /**
     * POST /api/meals
     * 식사 기록을 저장합니다.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealRecordResponse createMealRecord(@Valid @RequestBody MealRecordCreateRequest request) {
        return mealRecordService.create(request);
    }

    /**
     * GET /api/meals?profileId=1
     * GET /api/meals?profileId=1&date=2026-05-27
     * 프로필의 식사 기록을 조회합니다. date 파라미터가 있으면 해당 날짜만 반환합니다.
     */
    @GetMapping
    public List<MealRecordResponse> findMealRecords(
            @RequestParam Long profileId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return mealRecordService.findByProfile(profileId, date);
    }

    /**
     * GET /api/meals/summary?profileId=1&date=2026-05-27
     * 특정 날짜의 섭취 칼로리와 탄단지를 합산합니다.
     */
    @GetMapping("/summary")
    public MealSummaryResponse summarizeMealRecords(
            @RequestParam Long profileId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return mealRecordService.summarize(profileId, date);
    }

    /**
     * GET /api/meals/{id}
     * 단건 식사 기록을 조회합니다.
     */
    @GetMapping("/{id}")
    public MealRecordResponse findMealRecord(@PathVariable Long id) {
        return mealRecordService.findById(id);
    }

    /**
     * PUT /api/meals/{id}
     * 식사 기록을 수정합니다.
     */
    @PutMapping("/{id}")
    public MealRecordResponse updateMealRecord(
            @PathVariable Long id,
            @Valid @RequestBody MealRecordUpdateRequest request
    ) {
        return mealRecordService.update(id, request);
    }

    /**
     * DELETE /api/meals/{id}
     * 식사 기록을 삭제합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealRecord(@PathVariable Long id) {
        mealRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
