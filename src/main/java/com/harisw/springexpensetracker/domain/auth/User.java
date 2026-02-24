package com.harisw.springexpensetracker.domain.auth;

import java.time.Instant;
import java.util.UUID;

public record User(Long id, UUID publicId, String email, Role role, Instant createdAt) {
}
