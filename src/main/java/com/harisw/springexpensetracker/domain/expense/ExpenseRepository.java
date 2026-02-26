package com.harisw.springexpensetracker.domain.expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {
    Expense save(Expense expense);

    Optional<Expense> findByPublicIdAndEnvelopeId(UUID publicId, Long envelopeId);

    List<Expense> findByEnvelopeId(Long envelopeId);

    boolean deleteByPublicIdAndUserId(UUID publicId, Long userId);

    // List<Expense> findByDateRange(LocalDate from, LocalDate to);
}
