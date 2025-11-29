package com.fitracker.service;

import com.fitracker.dto.UpdateProfileRequest;
import com.fitracker.entity.User;
import com.fitracker.mapper.UserMapper;
import com.fitracker.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    UserMapper userMapper;

    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User updateProfile(Authentication auth, UpdateProfileRequest request) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userMapper.populateUser(user, request);

        return userRepository.save(user);
    }
}
