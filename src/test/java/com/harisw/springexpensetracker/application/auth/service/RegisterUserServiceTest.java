package com.harisw.springexpensetracker.application.auth.service;

import com.harisw.springexpensetracker.application.auth.dto.command.AuthCommand;
import com.harisw.springexpensetracker.application.auth.dto.response.AuthResponse;
import com.harisw.springexpensetracker.application.auth.port.TokenService;
import com.harisw.springexpensetracker.domain.auth.Role;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.auth.UserRepository;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterUserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private TokenService tokenService;

    private RegisterUserService service;

    @BeforeEach
    void setUp() {
        service = new RegisterUserService(repository, encoder, tokenService);

        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("hashedPassword");
    }

    @Test
    void create_shouldReturnSavedUser() throws JOSEException {
        AuthCommand command = new AuthCommand("TestUser@mail.com", "password123");

        User savedUser = new User(1L, UUID.randomUUID(), command.email(), Role.USER, Instant.now());

        // Stubs specific to this happy-path test
        when(repository.save(any(User.class), eq("hashedPassword"))).thenReturn(savedUser);
        when(tokenService.generateAccessToken(savedUser)).thenReturn("access-token");
        when(tokenService.generateRefreshToken(savedUser)).thenReturn("refresh-token");

        AuthResponse result = service.register(command);
        assertEquals(savedUser.publicId(), result.userPublicId());

    }
}
