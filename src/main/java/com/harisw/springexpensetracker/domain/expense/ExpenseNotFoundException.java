package com.harisw.springexpensetracker.domain.expense;

import java.util.UUID;

public class ExpenseNotFoundException extends RuntimeException {
    private final UUID publicId;

    public ExpenseNotFoundException(UUID publicId) {
        super("Expense not found: " + publicId);
        this.publicId = publicId;
    }

    public UUID getPublicId() {
        return publicId;
    }
}
