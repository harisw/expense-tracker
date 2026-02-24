package com.harisw.springexpensetracker.application.expense.dto.request;

import com.harisw.springexpensetracker.application.expense.dto.command.CreateExpenseCommand;
import com.harisw.springexpensetracker.domain.expense.ExpenseCategory;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @param category
 * @param description
 * @param amount
 * @param date
 */
public record CreateExpenseRequest(@NotNull ExpenseCategory category, @NotNull String description,
                                   @NotNull BigDecimal amount, @NotNull LocalDate date) {
    /**
     * @return CreateExpenseCommand
     */
    public CreateExpenseCommand toCommand() {
        return new CreateExpenseCommand(category, description, amount, date);
    }
}
