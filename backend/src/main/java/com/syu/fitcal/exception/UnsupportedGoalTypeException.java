package com.syu.fitcal.exception;

import com.syu.fitcal.domain.GoalType;

/**
 * {@link GoalType}에 대응하는 전략 빈이 등록되지 않았을 때 발생합니다.
 * (새 GoalType을 추가하고 전략 구현체를 등록하지 않은 경우 등)
 */
public class UnsupportedGoalTypeException extends RuntimeException {

    public UnsupportedGoalTypeException(GoalType goalType) {
        super("지원하지 않는 목표 유형입니다: " + goalType);
    }
}
