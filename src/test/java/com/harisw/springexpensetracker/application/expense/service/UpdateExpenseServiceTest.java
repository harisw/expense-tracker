package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.application.expense.dto.command.UpdateExpenseCommand;
import com.harisw.springexpensetracker.domain.auth.Role;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseCategory;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    private UpdateExpenseService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new UpdateExpenseService(repository);
        user = new User(1L, UUID.randomUUID(), "test@example.com", Role.USER, Instant.now());
    }

    @Test
    void update_shouldUpdateExpenseWithNewValues() {
        // given
        UUID publicId = UUID.randomUUID();
        Instant createdAt = Instant.now().minusSeconds(3600);

        Expense existing = new Expense(1L, user.id(), publicId, ExpenseCategory.FOOD, "Old description",
                new Money(new BigDecimal("10.00")), LocalDate.of(2024, 1, 1), createdAt);

        UpdateExpenseCommand command = new UpdateExpenseCommand(publicId, ExpenseCategory.TRANSPORT, "New description",
                new BigDecimal("25.00"), LocalDate.of(2024, 2, 15));

        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.of(existing));
        when(repository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Expense result = service.update(command, user);

        // then
        assertEquals(ExpenseCategory.TRANSPORT, result.category());
        assertEquals("New description", result.description());
        assertEquals(new BigDecimal("25.00"), result.amount().amount());
        assertEquals(LocalDate.of(2024, 2, 15), result.date());
    }

    @Test
    void update_shouldPreserveIdAndCreatedAt() {
        // given
        UUID publicId = UUID.randomUUID();
        Instant createdAt = Instant.now().minusSeconds(3600);

        Expense existing = new Expense(99L, user.id(), publicId, ExpenseCategory.FOOD, "Description",
                new Money(new BigDecimal("10.00")), LocalDate.now(), createdAt);

        UpdateExpenseCommand command = new UpdateExpenseCommand(publicId, ExpenseCategory.OTHER, "Updated",
                new BigDecimal("50.00"), LocalDate.now());

        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.of(existing));

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        // when
        service.update(command, user);

        // then
        Expense saved = captor.getValue();
        assertEquals(99L, saved.id());
        assertEquals(user.id(), saved.userId());
        assertEquals(publicId, saved.publicId());
        assertEquals(createdAt, saved.createdAt());
    }

    @Test
    void update_shouldThrowExpenseNotFoundExceptionWhenNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        UpdateExpenseCommand command = new UpdateExpenseCommand(publicId, ExpenseCategory.FOOD, "Description",
                new BigDecimal("10.00"), LocalDate.now());

        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.empty());

        // when & then
        ExpenseNotFoundException exception = assertThrows(ExpenseNotFoundException.class,
                () -> service.update(command, user));

        assertEquals(publicId, exception.getPublicId());
        verify(repository, never()).save(any());
    }
}
