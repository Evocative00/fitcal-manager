package com.syu.fitcal.service;

import com.syu.fitcal.domain.MealRecord;
import com.syu.fitcal.domain.UserProfile;
import com.syu.fitcal.dto.MealRecordCreateRequest;
import com.syu.fitcal.dto.MealRecordResponse;
import com.syu.fitcal.dto.MealRecordUpdateRequest;
import com.syu.fitcal.dto.MealSummaryResponse;
import com.syu.fitcal.exception.MealRecordNotFoundException;
import com.syu.fitcal.exception.ProfileNotFoundException;
import com.syu.fitcal.repository.MealRecordRepository;
import com.syu.fitcal.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MealRecordService {

    private final MealRecordRepository mealRecordRepository;
    private final UserProfileRepository userProfileRepository;

    public MealRecordService(
            MealRecordRepository mealRecordRepository,
            UserProfileRepository userProfileRepository
    ) {
        this.mealRecordRepository  = mealRecordRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public MealRecordResponse create(MealRecordCreateRequest request) {
        UserProfile profile = userProfileRepository.findById(request.profileId())
                .orElseThrow(() -> new ProfileNotFoundException(request.profileId()));

        MealRecord saved = mealRecordRepository.save(request.toEntity(profile));
        return MealRecordResponse.from(saved);
    }

    /**
     * 특정 프로필의 식사 기록을 조회합니다.
     *
     * @param profileId   조회할 프로필 ID
     * @param recordedDate 날짜 필터 (null이면 전체 조회)
     */
    public List<MealRecordResponse> findByProfile(Long profileId, LocalDate recordedDate) {
        if (recordedDate != null) {
            return mealRecordRepository
                    .findByUserProfileIdAndRecordedDateOrderByMealType(profileId, recordedDate)
                    .stream()
                    .map(MealRecordResponse::from)
                    .toList();
        }
        return mealRecordRepository
                .findByUserProfileIdOrderByRecordedDateDescCreatedAtDesc(profileId)
                .stream()
                .map(MealRecordResponse::from)
                .toList();
    }

    public MealRecordResponse findById(Long id) {
        MealRecord record = mealRecordRepository.findById(id)
                .orElseThrow(() -> new MealRecordNotFoundException(id));
        return MealRecordResponse.from(record);
    }

    /**
     * 식사 기록을 수정합니다 (PUT — 전체 필드 교체).
     */
    @Transactional
    public MealRecordResponse update(Long id, MealRecordUpdateRequest request) {
        MealRecord record = mealRecordRepository.findById(id)
                .orElseThrow(() -> new MealRecordNotFoundException(id));

        record.update(
                request.foodName().trim(),
                request.mealType(),
                request.calories(),
                request.proteinG(),
                request.carbsG(),
                request.fatG(),
                request.recordedDate()
        );
        return MealRecordResponse.from(record);
    }

    @Transactional
    public void delete(Long id) {
        if (!mealRecordRepository.existsById(id)) {
            throw new MealRecordNotFoundException(id);
        }
        mealRecordRepository.deleteById(id);
    }

    /**
     * 특정 프로필의 특정 날짜 식사 기록을 합산해 반환합니다.
     * 해당 날짜 기록이 없으면 모든 수치 0.0, mealCount 0으로 반환합니다.
     */
    public MealSummaryResponse getSummary(Long profileId, LocalDate date) {
        List<MealRecord> records = mealRecordRepository
                .findByUserProfileIdAndRecordedDateOrderByMealType(profileId, date);

        double consumedCalories = records.stream().mapToDouble(MealRecord::getCalories).sum();
        double consumedCarbs    = records.stream().mapToDouble(MealRecord::getCarbsG).sum();
        double consumedProtein  = records.stream().mapToDouble(MealRecord::getProteinG).sum();
        double consumedFat      = records.stream().mapToDouble(MealRecord::getFatG).sum();

        return new MealSummaryResponse(
                profileId,
                date,
                round(consumedCalories),
                round(consumedCarbs),
                round(consumedProtein),
                round(consumedFat),
                records.size()
        );
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
