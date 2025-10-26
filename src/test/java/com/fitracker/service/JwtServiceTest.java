package com.fitracker.service;

import com.fitracker.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;


    @Test
    void generateToken_shouldContainEmailClaim() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

        // When
        String token = jwtService.generateToken(user);

        // Then
        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // basic JWT format check
    }

    @Test
    void extractEmail_shouldReturnCorrectEmail() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

        String token = jwtService.generateToken(user);

        // When
        String extractedEmail = jwtService.extractEmail(token);

        // Then
        assertEquals("test@example.com", extractedEmail);
    }
}