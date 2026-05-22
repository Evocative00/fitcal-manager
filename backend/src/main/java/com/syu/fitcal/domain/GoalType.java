package com.syu.fitcal.domain;

public enum GoalType {
    DIET("다이어트 (체지방 감량)"),
    MAINTAIN("체중 유지"),
    BULK_UP("벌크업 (근육량 증가)"),
    HIGH_PROTEIN("고단백 식단");

    private final String label;

    GoalType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
