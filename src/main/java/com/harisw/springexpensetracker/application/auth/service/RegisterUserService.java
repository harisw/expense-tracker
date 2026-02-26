package com.harisw.springexpensetracker.application.auth.service;

import com.harisw.springexpensetracker.application.auth.dto.command.RegisterCommand;
import com.harisw.springexpensetracker.application.auth.dto.response.AuthResponse;
import com.harisw.springexpensetracker.application.auth.port.TokenService;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.auth.UserRepository;
import com.harisw.springexpensetracker.domain.auth.exception.DuplicateEmailException;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class RegisterUserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public RegisterUserService(UserRepository repository, PasswordEncoder passwordEncoder,
                               TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthResponse register(RegisterCommand cmd) throws JOSEException {
        if (repository.findByEmail(cmd.email()).isPresent()) {
            throw new DuplicateEmailException();
        }
        User user = new User(null, cmd.email(), cmd.name(), UUID.randomUUID(), Instant.now());
        String passwordHash = passwordEncoder.encode(cmd.password());

        User savedUser = repository.save(user, passwordHash);
        return new AuthResponse(
                tokenService.generateAccessToken(savedUser),
                tokenService.generateRefreshToken(savedUser),
                savedUser.publicId()
        );
    }
}
