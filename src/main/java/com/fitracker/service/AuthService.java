package com.fitracker.service;

import com.fitracker.dto.LoginRequest;
import com.fitracker.dto.RegisterRequest;
import com.fitracker.dto.RegisterResponse;
import com.fitracker.entity.User;
import com.fitracker.exception.ValidationException;
import com.fitracker.mapper.UserMapper;
import com.fitracker.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    private JwtService jwtService;

    public RegisterResponse register(@Valid RegisterRequest request) {
        validateUser(request.getEmail());
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        User user = userMapper.mapToUser(request);
        user.setId(id);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(now);

        userRepository.save(user);

        return new RegisterResponse(id, user.getEmail(), now);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return jwtService.generateToken(user);
    }

    private void validateUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new ValidationException("A user with this email already exists");
        }
        );
    }
}
