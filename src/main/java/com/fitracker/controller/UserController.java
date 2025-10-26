package com.fitracker.controller;

import com.fitracker.dto.UpdateProfileRequest;
import com.fitracker.dto.UserProfileResponse;
import com.fitracker.entity.User;
import com.fitracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
         User user = userService.getCurrentUser(authentication);
         UserProfileResponse response = new UserProfileResponse(user.getId(), user.getEmail(), user.getWeightKg(), user.getHeightCm(),
                 user.getAge(), user.getSex(), user.getGoal());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UpdateProfileRequest> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        User updated = userService.updateProfile(authentication, request);
        return ResponseEntity.ok(new UpdateProfileRequest(
                updated.getWeightKg(), updated.getHeightCm(), updated.getAge(),
                updated.getSex(), updated.getGoal()
        ));
    }
}