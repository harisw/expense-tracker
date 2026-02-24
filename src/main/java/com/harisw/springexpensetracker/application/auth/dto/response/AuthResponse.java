package com.harisw.springexpensetracker.application.auth.dto.response;

import java.util.UUID;

public record AuthResponse(String accessToken, String refreshToken, UUID userPublicId) {
}
