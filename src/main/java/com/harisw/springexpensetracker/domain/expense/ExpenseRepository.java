package com.harisw.springexpensetracker.domain.expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {
    Expense save(Expense expense);

    Optional<Expense> findByPublicIdAndUserId(UUID publicId, Long userId);

    List<Expense> findAll();

    List<Expense> findByUserId(Long userId);

    void deleteByPublicId(UUID publicId);

    // List<Expense> findByDateRange(LocalDate from, LocalDate to);
}
