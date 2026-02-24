package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.application.expense.dto.command.UpdateExpenseCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseNotFoundException;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateExpenseService {
    private final ExpenseRepository repository;

    public UpdateExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense update(UpdateExpenseCommand cmd, User user) {
        Expense existing = repository.findByPublicIdAndUserId(cmd.publicId(), user.id())
                .orElseThrow(() -> new ExpenseNotFoundException(cmd.publicId()));
        Expense updated = new Expense(existing.id(), existing.userId(), existing.publicId(), cmd.category(),
                cmd.description(), new Money(cmd.amount()), cmd.date(), existing.createdAt());

        return repository.save(updated);
    }
}
