package com.harisw.springexpensetracker.application.expense.dto.request;

import com.harisw.springexpensetracker.application.expense.dto.command.CreateExpenseCommand;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @param envelopePublicId
 * @param description
 * @param amount
 * @param date
 */
public record CreateExpenseRequest(@NotNull UUID envelopePublicId, @NotNull String description,
                                   @NotNull BigDecimal amount, @NotNull LocalDate date) {
    /**
     * @return CreateExpenseCommand
     */
    public CreateExpenseCommand toCommand() {
        return new CreateExpenseCommand(envelopePublicId, description, amount, date);
    }
}
