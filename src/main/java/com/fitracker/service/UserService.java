package com.fitracker.service;

import com.fitracker.dto.UpdateProfileRequest;
import com.fitracker.entity.User;
import com.fitracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User updateProfile(Authentication auth, UpdateProfileRequest request) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getWeightKg() != null) user.setWeightKg(request.getWeightKg());
        if (request.getHeightCm() != null) user.setHeightCm(request.getHeightCm());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getSex() != null) user.setSex(request.getSex());
        if (request.getGoal() != null) user.setGoal(request.getGoal());

        return userRepository.save(user);
    }
}
