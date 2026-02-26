package com.harisw.springexpensetracker.infrastructure.persistence.expense;

import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ExpenseRepositoryImpl implements ExpenseRepository {

    private final ExpenseJpaRepository jpa;

    public ExpenseRepositoryImpl(ExpenseJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Expense save(Expense expense) {
        return ExpenseMapper.toDomain(jpa.save(ExpenseMapper.toEntity(expense)));
    }

    @Override
    public Optional<Expense> findByPublicIdAndEnvelopeId(UUID publicId, Long envelopeId) {
        return jpa.findByPublicIdAndEnvelopeId(publicId, envelopeId).map(ExpenseMapper::toDomain);
    }

    @Override
    public List<Expense> findByEnvelopeId(Long envelopeId) {
        return jpa.findByEnvelopeId(envelopeId).stream().map(ExpenseMapper::toDomain).toList(); // Java 16+
    }

    @Override
    public boolean deleteByPublicIdAndUserId(UUID publicId, Long userId) {
        return jpa.deleteByPublicIdAndUserId(publicId, userId) > 0;
    }
}
