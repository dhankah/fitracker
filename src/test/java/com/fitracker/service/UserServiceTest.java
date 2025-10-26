package com.fitracker.service;

import com.fitracker.dto.UpdateProfileRequest;
import com.fitracker.entity.User;
import com.fitracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Test
    void getCurrentUser_shouldReturnUserByEmail() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        User result = userService.getCurrentUser(authentication);

        // Then
        assertEquals(email, result.getEmail());
    }

    @Test
    void getCurrentUser_shouldThrowIfUserNotFound() {
        // Given
        when(authentication.getName()).thenReturn("missing@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getCurrentUser(authentication);
        });
    }

    @Test
    void updateProfile_shouldUpdateOnlyProvidedFields() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setWeightKg(70.0);
        user.setHeightCm(175);
        user.setAge(25);
        user.setSex("male");
        user.setGoal("gain");

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setWeightKg(75.0); // only weight is updated

        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        // When
        User updated = userService.updateProfile(authentication, request);

        // Then
        assertEquals(75.0, updated.getWeightKg());
        assertEquals(175, updated.getHeightCm()); // unchanged
        assertEquals("male", updated.getSex());   // unchanged
    }
}