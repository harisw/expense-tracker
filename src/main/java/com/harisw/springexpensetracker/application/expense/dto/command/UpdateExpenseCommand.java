package com.harisw.springexpensetracker.application.expense.dto.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateExpenseCommand(UUID publicId, UUID envelopePublicId, String description, BigDecimal amount,
                                   LocalDate date) {
}
