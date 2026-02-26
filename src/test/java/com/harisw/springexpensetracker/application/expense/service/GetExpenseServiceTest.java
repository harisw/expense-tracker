package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseNotFoundException;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @Mock
    private EnvelopeRepository envelopeRepository;

    private GetExpenseService service;
    private User user;
    private Envelope envelope;

    @BeforeEach
    void setUp() {
        service = new GetExpenseService(repository, envelopeRepository);
        user = new User(1L, "test@example.com", "Test User", UUID.randomUUID(), Instant.now());
        envelope = new Envelope(10L, user.id(), null, UUID.randomUUID(), "Groceries",
                false, new Money(new BigDecimal("500.00")), false, Instant.now());
    }

    @Test
    void get_shouldReturnExpenseWhenFound() {
        // given
        UUID publicId = UUID.randomUUID();
        Expense expense = expense(1L, publicId);

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));
        when(repository.findByPublicIdAndEnvelopeId(publicId, envelope.id()))
                .thenReturn(Optional.of(expense));

        // when
        Expense result = service.get(envelope.publicId(), publicId, user);

        // then
        assertEquals(expense, result);
        verify(repository).findByPublicIdAndEnvelopeId(publicId, envelope.id());
    }

    @Test
    void get_shouldThrowEnvelopeNotFoundExceptionWhenEnvelopeNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(EnvelopeNotFoundException.class,
                () -> service.get(envelope.publicId(), publicId, user));
    }

    @Test
    void get_shouldThrowExpenseNotFoundExceptionWhenExpenseNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));
        when(repository.findByPublicIdAndEnvelopeId(publicId, envelope.id()))
                .thenReturn(Optional.empty());

        // when & then
        ExpenseNotFoundException exception = assertThrows(ExpenseNotFoundException.class,
                () -> service.get(envelope.publicId(), publicId, user));

        assertEquals(publicId, exception.getPublicId());
    }

    @Test
    void getAllByEnvelopeId_shouldReturnAllExpenses() {
        // given
        List<Expense> expenses = List.of(
                expense(1L, UUID.randomUUID()),
                expense(2L, UUID.randomUUID()));

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));
        when(repository.findByEnvelopeId(envelope.id())).thenReturn(expenses);

        // when
        List<Expense> result = service.getAllByEnvelopeId(envelope.publicId(), user);

        // then
        assertEquals(2, result.size());
        assertEquals(expenses, result);
        verify(repository).findByEnvelopeId(envelope.id());
    }

    @Test
    void getAllByEnvelopeId_shouldReturnEmptyListWhenNoExpenses() {
        // given
        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));
        when(repository.findByEnvelopeId(envelope.id())).thenReturn(List.of());

        // when
        List<Expense> result = service.getAllByEnvelopeId(envelope.publicId(), user);

        // then
        assertTrue(result.isEmpty());
    }

    private Expense expense(Long id, UUID publicId) {
        return new Expense(id, envelope.id(), publicId, "Test expense",
                new Money(new BigDecimal("20.00")), LocalDate.now(), Instant.now());
    }
}
