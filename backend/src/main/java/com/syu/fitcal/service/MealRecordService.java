package com.syu.fitcal.service;

import com.syu.fitcal.domain.MealRecord;
import com.syu.fitcal.domain.UserProfile;
import com.syu.fitcal.dto.MealRecordCreateRequest;
import com.syu.fitcal.dto.MealRecordResponse;
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

    @Transactional
    public void delete(Long id) {
        if (!mealRecordRepository.existsById(id)) {
            throw new MealRecordNotFoundException(id);
        }
        mealRecordRepository.deleteById(id);
    }
}
