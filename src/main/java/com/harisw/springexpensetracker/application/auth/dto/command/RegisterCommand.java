package com.harisw.springexpensetracker.application.auth.dto.command;

public record RegisterCommand(String email, String name, String password) {
}
