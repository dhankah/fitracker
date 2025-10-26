package com.fitracker.service;

import com.fitracker.dto.RegisterRequest;
import com.fitracker.dto.RegisterResponse;
import com.fitracker.entity.User;
import com.fitracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        RegisterResponse response = authService.register(request);

        // Then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals(encodedPassword, savedUser.getPasswordHash());
        assertEquals(request.getWeightKg(), savedUser.getWeightKg());
        assertEquals(request.getHeightCm(), savedUser.getHeightCm());
        assertEquals(request.getAge(), savedUser.getAge());
        assertEquals(request.getSex(), savedUser.getSex());
        assertEquals(request.getGoal(), savedUser.getGoal());

        assertEquals(savedUser.getId(), response.getId());
        assertEquals(savedUser.getEmail(), response.getEmail());
        assertEquals(savedUser.getCreatedAt(), response.getCreatedAt());
    }

}
