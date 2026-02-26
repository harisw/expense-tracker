package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.application.expense.dto.command.CreateExpenseCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @Mock
    private EnvelopeRepository envelopeRepository;

    private CreateExpenseService service;
    private User user;
    private Envelope envelope;

    @BeforeEach
    void setUp() {
        service = new CreateExpenseService(repository, envelopeRepository);
        user = new User(1L, "test@example.com", "Test User", UUID.randomUUID(), Instant.now());
        envelope = new Envelope(10L, user.id(), null, UUID.randomUUID(), "Groceries",
                false, new Money(new BigDecimal("500.00")), false, Instant.now());
    }

    @Test
    void create_shouldReturnSavedExpense() {
        // given
        CreateExpenseCommand command = new CreateExpenseCommand(envelope.publicId(), "Lunch at restaurant",
                new BigDecimal("25.50"), LocalDate.of(2024, 1, 15));

        Expense savedExpense = new Expense(1L, envelope.id(), UUID.randomUUID(), "Lunch at restaurant",
                new Money(new BigDecimal("25.50")), LocalDate.of(2024, 1, 15), Instant.now());

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));
        when(repository.save(any(Expense.class))).thenReturn(savedExpense);

        // when
        Expense result = service.create(command, user);

        // then
        assertEquals(savedExpense, result);
    }

    @Test
    void create_shouldPassCorrectExpenseToRepository() {
        // given
        CreateExpenseCommand command = new CreateExpenseCommand(envelope.publicId(), "Bus ticket",
                new BigDecimal("2.00"), LocalDate.of(2024, 2, 20));

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));

        // when
        service.create(command, user);

        // then
        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(repository).save(captor.capture());

        Expense captured = captor.getValue();
        assertNull(captured.id());
        assertEquals(envelope.id(), captured.envelopeId());
        assertNotNull(captured.publicId());
        assertEquals("Bus ticket", captured.description());
        assertEquals(new BigDecimal("2.00"), captured.amount().amount());
        assertEquals(LocalDate.of(2024, 2, 20), captured.date());
        assertNotNull(captured.createdAt());
    }

    @Test
    void create_withNonExistentEnvelope_shouldThrowEnvelopeNotFoundException() {
        // given
        UUID unknownEnvelopePublicId = UUID.randomUUID();
        CreateExpenseCommand command = new CreateExpenseCommand(unknownEnvelopePublicId, "Lunch",
                new BigDecimal("10.00"), LocalDate.now());

        when(envelopeRepository.findByPublicIdAndUserId(unknownEnvelopePublicId, user.id()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(EnvelopeNotFoundException.class, () -> service.create(command, user));
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldGenerateUniquePublicId() {
        // given
        CreateExpenseCommand command = new CreateExpenseCommand(envelope.publicId(), "Movie ticket",
                new BigDecimal("15.00"), LocalDate.now());

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));

        // when
        service.create(command, user);
        service.create(command, user);

        // then
        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(repository, times(2)).save(captor.capture());

        var captured = captor.getAllValues();
        assertNotEquals(captured.get(0).publicId(), captured.get(1).publicId());
    }

    @Test
    void create_shouldSetCreatedAtToCurrentTime() {
        // given
        Instant before = Instant.now();
        CreateExpenseCommand command = new CreateExpenseCommand(envelope.publicId(), "Electric bill",
                new BigDecimal("100.00"), LocalDate.now());

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));

        // when
        service.create(command, user);
        Instant after = Instant.now();

        // then
        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(repository).save(captor.capture());

        Instant createdAt = captor.getValue().createdAt();
        assertTrue(createdAt.compareTo(before) >= 0);
        assertTrue(createdAt.compareTo(after) <= 0);
    }
}
