package com.harisw.springexpensetracker.domain.auth;

import java.time.Instant;
import java.util.UUID;

public record User(Long id, String email, String name, UUID publicId, Instant createdAt) {
}
