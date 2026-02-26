package com.harisw.springexpensetracker.application.auth.dto.request;

import com.harisw.springexpensetracker.application.auth.dto.command.RegisterCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Email String email, @NotBlank @Size(max = 255) String name, @NotBlank @Size(min = 8) String password) {
    /**
     * @return AuthCommand
     */
    public RegisterCommand toCommand() {
        return new RegisterCommand(email, name, password);
    }
}
