package com.syu.fitcal.domain;

public enum ActivityLevel {
    LOW(1.2),
    NORMAL(1.55),
    HIGH(1.725),
    VERY_HIGH(1.9);

    private final double multiplier;

    ActivityLevel(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
