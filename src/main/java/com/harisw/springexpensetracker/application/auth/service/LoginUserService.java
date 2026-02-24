package com.harisw.springexpensetracker.application.auth.service;

import com.harisw.springexpensetracker.application.auth.dto.command.AuthCommand;
import com.harisw.springexpensetracker.application.auth.dto.response.AuthResponse;
import com.harisw.springexpensetracker.application.auth.port.TokenService;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.auth.UserRepository;
import com.harisw.springexpensetracker.domain.auth.exception.InvalidCredentialsException;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginUserService(UserRepository repository, PasswordEncoder passwordEncoder,
                            TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthResponse login(AuthCommand cmd) throws JOSEException {
        User user = repository.findByEmail(cmd.email())
                .orElseThrow(InvalidCredentialsException::new);

        String storedHash = repository.getPasswordHashByEmail(cmd.email());
        if (!passwordEncoder.matches(cmd.password(), storedHash)) {
            throw new InvalidCredentialsException();
        }


        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, user.publicId());

    }
}
