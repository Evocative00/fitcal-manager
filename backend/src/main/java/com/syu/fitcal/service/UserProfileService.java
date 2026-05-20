package com.syu.fitcal.service;

import com.syu.fitcal.domain.UserProfile;
import com.syu.fitcal.dto.UserProfileCreateRequest;
import com.syu.fitcal.dto.UserProfileResponse;
import com.syu.fitcal.exception.ProfileNotFoundException;
import com.syu.fitcal.repository.UserProfileRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfileResponse create(UserProfileCreateRequest request) {
        UserProfile savedProfile = userProfileRepository.save(request.toEntity());
        return UserProfileResponse.from(savedProfile);
    }

    public List<UserProfileResponse> findAll() {
        return userProfileRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(UserProfileResponse::from)
                .toList();
    }

    public UserProfileResponse findById(Long id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));
        return UserProfileResponse.from(userProfile);
    }
}
