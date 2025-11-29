package com.fitracker.service;

import com.fitracker.dto.RegisterRequest;
import com.fitracker.dto.RegisterResponse;
import com.fitracker.entity.User;
import com.fitracker.mapper.UserMapper;
import com.fitracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Test
    void register_shouldCreateUserAndReturnResponse() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("secret");
        request.setWeightKg(75.0);
        request.setHeightCm(180);
        request.setAge(30);
        request.setSex("male");
        request.setGoal("gain");

        String encodedPassword = "$2a$10$encodedPassword";
        when(passwordEncoder.encode("secret")).thenReturn(encodedPassword);

        var user = User.builder()
                .age(30)
                .email("test@example.com")
                .weightKg(75.0)
                .heightCm(180)
                .goal("gain")
                .sex("male")
                .build();
        doReturn(user).when(userMapper).mapToUser(request);

        // When
        authService.register(request);

        // Then
        verify(userRepository).save(user);
    }

}
