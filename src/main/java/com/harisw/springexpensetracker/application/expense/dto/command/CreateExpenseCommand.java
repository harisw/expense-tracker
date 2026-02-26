package com.harisw.springexpensetracker.application.expense.dto.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateExpenseCommand(UUID envelopePublicId, String description, BigDecimal amount, LocalDate date) {
}
