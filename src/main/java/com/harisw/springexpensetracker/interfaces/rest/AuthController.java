package com.harisw.springexpensetracker.interfaces.rest;

import com.harisw.springexpensetracker.application.auth.dto.request.LoginRequest;
import com.harisw.springexpensetracker.application.auth.dto.request.RegisterRequest;
import com.harisw.springexpensetracker.application.auth.dto.response.AuthResponse;
import com.harisw.springexpensetracker.application.auth.service.LoginUserService;
import com.harisw.springexpensetracker.application.auth.service.RegisterUserService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterUserService register;
    private final LoginUserService login;

    public AuthController(RegisterUserService register, LoginUserService login) {
        this.register = register;
        this.login = login;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) throws JOSEException {
        return register.register(req.toCommand());
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse login(@Valid @RequestBody LoginRequest req) throws JOSEException {
        return login.login(req.toCommand());
    }

}
