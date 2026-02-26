package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.application.expense.dto.command.UpdateExpenseCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseNotFoundException;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateExpenseService {
    private final ExpenseRepository repository;
    private final EnvelopeRepository envelopeRepository;

    public UpdateExpenseService(ExpenseRepository repository, EnvelopeRepository envelopeRepository) {
        this.repository = repository;
        this.envelopeRepository = envelopeRepository;
    }

    public Expense update(UpdateExpenseCommand cmd, User user) {
        Envelope envelope = envelopeRepository.findByPublicIdAndUserId(cmd.envelopePublicId(), user.id())
                .orElseThrow(() -> new EnvelopeNotFoundException(cmd.envelopePublicId()));

        Expense existing = repository.findByPublicIdAndEnvelopeId(cmd.publicId(), envelope.id())
                .orElseThrow(() -> new ExpenseNotFoundException(cmd.publicId()));

        Expense updated = new Expense(existing.id(), envelope.id(), existing.publicId(),
                cmd.description(), new Money(cmd.amount()), cmd.date(), existing.createdAt());

        return repository.save(updated);
    }
}
