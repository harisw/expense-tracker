package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.application.expense.dto.command.CreateExpenseCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


/**
 *
 */
@Service
@Transactional
public class CreateExpenseService {
    private final ExpenseRepository repository;

    public CreateExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense create(CreateExpenseCommand cmd, User user) {
        Expense expense = new Expense(null, user.id(), UUID.randomUUID(), cmd.category(), cmd.description(),
                new Money(cmd.amount()), cmd.date(), Instant.now());

        return repository.save(expense);
    }
}
