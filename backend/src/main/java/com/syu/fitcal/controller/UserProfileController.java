package com.syu.fitcal.controller;

import com.syu.fitcal.dto.UserProfileCreateRequest;
import com.syu.fitcal.dto.UserProfileResponse;
import com.syu.fitcal.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileResponse createProfile(@Valid @RequestBody UserProfileCreateRequest request) {
        return userProfileService.create(request);
    }

    @GetMapping
    public List<UserProfileResponse> findProfiles() {
        return userProfileService.findAll();
    }

    @GetMapping("/{id}")
    public UserProfileResponse findProfile(@PathVariable Long id) {
        return userProfileService.findById(id);
    }
}
