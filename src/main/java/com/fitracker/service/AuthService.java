package com.fitracker.service;

import com.fitracker.dto.RegisterRequest;
import com.fitracker.dto.RegisterResponse;
import com.fitracker.entity.User;
import com.fitracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        User user = new User();
        user.setId(id);
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setWeightKg(request.getWeightKg());
        user.setHeightCm(request.getHeightCm());
        user.setAge(request.getAge());
        user.setSex(request.getSex());
        user.setGoal(request.getGoal());
        user.setCreatedAt(now);

        userRepository.save(user);

        return new RegisterResponse(id, user.getEmail(), now);
    }
}
