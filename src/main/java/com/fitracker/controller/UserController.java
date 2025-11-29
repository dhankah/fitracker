package com.fitracker.controller;

import com.fitracker.dto.UpdateProfileRequest;
import com.fitracker.dto.UserProfileResponse;
import com.fitracker.entity.User;
import com.fitracker.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
         User user = userService.getCurrentUser(authentication);
         UserProfileResponse response = UserProfileResponse.builder()
                 .id(user.getId())
                 .email(user.getEmail())
                 .weightKg(user.getWeightKg())
                 .heightCm(user.getHeightCm())
                 .age(user.getAge())
                 .sex(user.getSex())
                 .goal(user.getGoal())
                 .build();

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