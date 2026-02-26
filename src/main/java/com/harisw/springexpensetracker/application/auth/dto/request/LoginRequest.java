package com.harisw.springexpensetracker.application.auth.dto.request;

import com.harisw.springexpensetracker.application.auth.dto.command.LoginCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    /**
     * @return AuthCommand
     */
    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
