package com.harisw.springexpensetracker.application.auth.dto.request;

import com.harisw.springexpensetracker.application.auth.dto.command.AuthCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    /**
     * @return AuthCommand
     */
    public AuthCommand toCommand() {
        return new AuthCommand(email, password);
    }
}
