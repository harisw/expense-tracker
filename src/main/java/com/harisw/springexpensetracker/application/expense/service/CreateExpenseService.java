package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.application.expense.dto.command.CreateExpenseCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
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
    private final EnvelopeRepository envelopeRepository;

    public CreateExpenseService(ExpenseRepository repository, EnvelopeRepository envelopeRepository) {
        this.repository = repository;
        this.envelopeRepository = envelopeRepository;
    }

    public Expense create(CreateExpenseCommand cmd, User user) {
        Envelope envelope = this.envelopeRepository.findByPublicIdAndUserId(cmd.envelopePublicId(), user.id())
                .orElseThrow(() -> new EnvelopeNotFoundException(cmd.envelopePublicId()));
        Expense expense = new Expense(null, envelope.id(), UUID.randomUUID(), cmd.description(),
                new Money(cmd.amount()), cmd.date(), Instant.now());

        return repository.save(expense);
    }
}
