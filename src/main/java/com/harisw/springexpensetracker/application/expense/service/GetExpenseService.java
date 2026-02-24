package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseNotFoundException;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetExpenseService {
    private final ExpenseRepository repository;

    public GetExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense get(UUID publicId, User user) {
        return repository.findByPublicIdAndUserId(publicId, user.id())
                .orElseThrow(() -> new ExpenseNotFoundException(publicId));
    }

    public List<Expense> getAll() {
        return repository.findAll();
    }

    public List<Expense> getAllByUserId(User user) {
        return repository.findByUserId(user.id());
    }
}
