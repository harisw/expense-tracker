package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
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
    private final EnvelopeRepository envelopeRepository;

    public GetExpenseService(ExpenseRepository repository, EnvelopeRepository envelopeRepository) {
        this.repository = repository;
        this.envelopeRepository = envelopeRepository;
    }

    public Expense get(UUID envelopePublicId, UUID publicId, User user) {
        Envelope envelope = envelopeRepository.findByPublicIdAndUserId(envelopePublicId, user.id())
                .orElseThrow(() -> new EnvelopeNotFoundException(envelopePublicId));
        return repository.findByPublicIdAndEnvelopeId(publicId, envelope.id())
                .orElseThrow(() -> new ExpenseNotFoundException(publicId));
    }

    public List<Expense> getAllByEnvelopeId(UUID envelopePublicId, User user) {
        Envelope envelope = this.envelopeRepository.findByPublicIdAndUserId(envelopePublicId, user.id())
                .orElseThrow(() -> new EnvelopeNotFoundException(envelopePublicId));
        return repository.findByEnvelopeId(envelope.id());
    }

//    public List<Expense> getAllByUserId(User user) {
//        return repository.findByUserId(user.id());
//    }
}
