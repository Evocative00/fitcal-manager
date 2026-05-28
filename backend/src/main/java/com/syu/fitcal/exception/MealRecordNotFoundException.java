package com.syu.fitcal.exception;

public class MealRecordNotFoundException extends RuntimeException {

    public MealRecordNotFoundException(Long id) {
        super("식사 기록을 찾을 수 없습니다. id=" + id);
    }
}
