package com.syu.fitcal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 식사 기록 엔티티.
 *
 * <p>사용자({@link UserProfile})가 특정 날짜에 섭취한 음식의 영양 정보를 저장합니다.
 * 하루 합산 조회로 목표 대비 실제 섭취량을 비교할 수 있습니다.</p>
 */
@Entity
@Table(name = "meal_records")
public class MealRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(nullable = false, length = 100)
    private String foodName;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 20)
    private MealType mealType;

    @Column(nullable = false)
    private Double calories;

    @Column(name = "protein_g", nullable = false)
    private Double proteinG;

    @Column(name = "carbs_g", nullable = false)
    private Double carbsG;

    @Column(name = "fat_g", nullable = false)
    private Double fatG;

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected MealRecord() {}

    public MealRecord(
            UserProfile userProfile,
            String foodName,
            MealType mealType,
            Double calories,
            Double proteinG,
            Double carbsG,
            Double fatG,
            LocalDate recordedDate
    ) {
        this.userProfile  = userProfile;
        this.foodName     = foodName;
        this.mealType     = mealType;
        this.calories     = calories;
        this.proteinG     = proteinG;
        this.carbsG       = carbsG;
        this.fatG         = fatG;
        this.recordedDate = recordedDate;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * PUT 요청에 대응하는 전체 필드 갱신.
     * recordedDate가 null이면 기존 날짜를 유지합니다.
     */
    public void update(String foodName, MealType mealType, Double calories,
                       Double proteinG, Double carbsG, Double fatG, LocalDate recordedDate) {
        this.foodName  = foodName;
        this.mealType  = mealType;
        this.calories  = calories;
        this.proteinG  = proteinG;
        this.carbsG    = carbsG;
        this.fatG      = fatG;
        if (recordedDate != null) {
            this.recordedDate = recordedDate;
        }
    }

    public Long getId()                 { return id; }
    public UserProfile getUserProfile() { return userProfile; }
    public String getFoodName()         { return foodName; }
    public MealType getMealType()       { return mealType; }
    public Double getCalories()         { return calories; }
    public Double getProteinG()         { return proteinG; }
    public Double getCarbsG()           { return carbsG; }
    public Double getFatG()             { return fatG; }
    public LocalDate getRecordedDate()  { return recordedDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
