package com.syu.fitcal.exception;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(Long id) {
        super("프로필을 찾을 수 없습니다. id=" + id);
    }
}
