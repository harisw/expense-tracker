package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.application.expense.dto.command.UpdateExpenseCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @Mock
    private EnvelopeRepository envelopeRepository;

    private UpdateExpenseService service;
    private User user;
    private Envelope envelope;

    @BeforeEach
    void setUp() {
        service = new UpdateExpenseService(repository, envelopeRepository);
        user = new User(1L, "test@example.com", "Test User", UUID.randomUUID(), Instant.now());
        envelope = new Envelope(10L, user.id(), null, UUID.randomUUID(), "Groceries",
                false, new Money(new BigDecimal("500.00")), false, Instant.now());
    }

    @Test
    void update_shouldUpdateExpenseWithNewValues() {
        // given
        UUID publicId = UUID.randomUUID();
        Expense existing = new Expense(1L, envelope.id(), publicId, "Old description",
                new Money(new BigDecimal("10.00")), LocalDate.of(2024, 1, 1), Instant.now().minusSeconds(3600));

        UpdateExpenseCommand command = new UpdateExpenseCommand(publicId, envelope.publicId(),
                "New description", new BigDecimal("25.00"), LocalDate.of(2024, 2, 15));

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));
        when(repository.findByPublicIdAndEnvelopeId(publicId, envelope.id()))
                .thenReturn(Optional.of(existing));
        when(repository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Expense result = service.update(command, user);

        // then
        assertEquals("New description", result.description());
        assertEquals(new BigDecimal("25.00"), result.amount().amount());
        assertEquals(LocalDate.of(2024, 2, 15), result.date());
    }

    @Test
    void update_shouldPreserveImmutableFields() {
        // given
        UUID publicId = UUID.randomUUID();
        Instant originalCreatedAt = Instant.parse("2024-01-01T00:00:00Z");
        Expense existing = new Expense(99L, envelope.id(), publicId, "Description",
                new Money(new BigDecimal("10.00")), LocalDate.now(), originalCreatedAt);

        UpdateExpenseCommand command = new UpdateExpenseCommand(publicId, envelope.publicId(),
                "Updated", new BigDecimal("50.00"), LocalDate.now());

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));
        when(repository.findByPublicIdAndEnvelopeId(publicId, envelope.id()))
                .thenReturn(Optional.of(existing));

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        // when
        service.update(command, user);

        // then
        Expense saved = captor.getValue();
        assertEquals(99L, saved.id());
        assertEquals(envelope.id(), saved.envelopeId());
        assertEquals(publicId, saved.publicId());
        assertEquals(originalCreatedAt, saved.createdAt());
    }

    @Test
    void update_shouldThrowExpenseNotFoundExceptionWhenNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        UpdateExpenseCommand command = new UpdateExpenseCommand(publicId, envelope.publicId(),
                "Description", new BigDecimal("10.00"), LocalDate.now());

        when(envelopeRepository.findByPublicIdAndUserId(envelope.publicId(), user.id()))
                .thenReturn(Optional.of(envelope));
        when(repository.findByPublicIdAndEnvelopeId(publicId, envelope.id()))
                .thenReturn(Optional.empty());

        // when & then
        ExpenseNotFoundException exception = assertThrows(ExpenseNotFoundException.class,
                () -> service.update(command, user));

        assertEquals(publicId, exception.getPublicId());
        verify(repository, never()).save(any());
    }
}
