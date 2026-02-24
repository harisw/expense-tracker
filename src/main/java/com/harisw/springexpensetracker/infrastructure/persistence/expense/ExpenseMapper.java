package com.harisw.springexpensetracker.infrastructure.persistence.expense;

import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.infrastructure.persistence.auth.UserJpaEntity;

public final class ExpenseMapper {
    private ExpenseMapper() {
    }

    public static ExpenseJpaEntity toEntity(Expense e) {
        ExpenseJpaEntity jpa = new ExpenseJpaEntity();
        jpa.setId(e.id());

        UserJpaEntity userRef = new UserJpaEntity();
        userRef.setId(e.userId());
        jpa.setUser(userRef
        );
        jpa.setPublicId(e.publicId());
        jpa.setDescription(e.description());
        jpa.setAmount(e.amount().amount());
        jpa.setCategory(e.category());
        jpa.setDate(e.date());
        jpa.setCreatedAt(e.createdAt());
        return jpa;
    }

    public static Expense toDomain(ExpenseJpaEntity e) {
        return new Expense(e.getId(), e.getUser().getId(), e.getPublicId(), e.getCategory(), e.getDescription(),
                new Money(e.getAmount()),
                e.getDate(), e.getCreatedAt());
    }
}
