package com.harisw.springexpensetracker.domain.expense;

import com.harisw.springexpensetracker.domain.common.Money;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Expense(Long id, Long envelopeId, UUID publicId, String description, Money amount,
                      LocalDate date, Instant createdAt) {
}
