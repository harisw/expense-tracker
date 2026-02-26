package com.harisw.springexpensetracker.domain.envelope;

import com.harisw.springexpensetracker.domain.common.Money;

import java.time.Instant;
import java.util.UUID;

public record Envelope(Long id, Long userId, Long parentId, UUID publicId, String name,
                       Boolean isTemplate, Money budget, Boolean canNotify, Instant createdAt) {
}
