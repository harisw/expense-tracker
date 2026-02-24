package com.harisw.springexpensetracker.application.auth.dto.request;

import com.harisw.springexpensetracker.application.auth.dto.command.AuthCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Email String email, @NotBlank @Size(min = 8) String password) {
    /**
     * @return AuthCommand
     */
    public AuthCommand toCommand() {
        return new AuthCommand(email, password);
    }
}
