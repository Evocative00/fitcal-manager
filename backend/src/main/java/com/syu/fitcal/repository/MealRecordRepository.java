package com.syu.fitcal.repository;

import com.syu.fitcal.domain.MealRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {

    /** 특정 프로필의 전체 식사 기록을 날짜 역순으로 조회합니다. */
    List<MealRecord> findByUserProfileIdOrderByRecordedDateDescCreatedAtDesc(Long profileId);

    /** 특정 프로필의 특정 날짜 식사 기록을 식사 유형 순으로 조회합니다. */
    List<MealRecord> findByUserProfileIdAndRecordedDateOrderByMealType(Long profileId, LocalDate recordedDate);
}
