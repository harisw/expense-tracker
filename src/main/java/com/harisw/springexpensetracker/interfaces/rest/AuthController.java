package com.harisw.springexpensetracker.interfaces.rest;

import com.harisw.springexpensetracker.application.auth.dto.request.LoginRequest;
import com.harisw.springexpensetracker.application.auth.dto.request.RegisterRequest;
import com.harisw.springexpensetracker.application.auth.dto.response.AuthResponse;
import com.harisw.springexpensetracker.application.auth.service.DeleteUserService;
import com.harisw.springexpensetracker.application.auth.service.LoginUserService;
import com.harisw.springexpensetracker.application.auth.service.RegisterUserService;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.auth.exception.InvalidCredentialsException;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterUserService register;
    private final LoginUserService login;
    private final DeleteUserService delete;

    public AuthController(RegisterUserService register, LoginUserService login, DeleteUserService delete) {
        this.register = register;
        this.login = login;
        this.delete = delete;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse login(@Valid @RequestBody LoginRequest req) throws JOSEException {
        return login.login(req.toCommand());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) throws JOSEException {
        return register.register(req.toCommand());
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public User me(Authentication authentication) throws JOSEException {
        return (User) authentication.getPrincipal();
    }

    @DeleteMapping("/{publicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID publicId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!publicId.equals(user.publicId())) {
            throw new InvalidCredentialsException();
        }
        delete.delete(publicId);
    }
}
