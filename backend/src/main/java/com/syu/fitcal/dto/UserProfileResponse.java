package com.syu.fitcal.dto;

import com.syu.fitcal.domain.ActivityLevel;
import com.syu.fitcal.domain.Gender;
import com.syu.fitcal.domain.GoalType;
import com.syu.fitcal.domain.UserProfile;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String name,
        Integer age,
        Double heightCm,
        Double weightKg,
        Gender gender,
        ActivityLevel activityLevel,
        GoalType goalType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserProfileResponse from(UserProfile userProfile) {
        return new UserProfileResponse(
                userProfile.getId(),
                userProfile.getName(),
                userProfile.getAge(),
                userProfile.getHeightCm(),
                userProfile.getWeightKg(),
                userProfile.getGender(),
                userProfile.getActivityLevel(),
                userProfile.getGoalType(),
                userProfile.getCreatedAt(),
                userProfile.getUpdatedAt()
        );
    }
}
