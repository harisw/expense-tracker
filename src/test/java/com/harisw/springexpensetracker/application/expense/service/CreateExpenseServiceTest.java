package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.application.expense.dto.command.CreateExpenseCommand;
import com.harisw.springexpensetracker.domain.auth.Role;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.expense.Expense;
import com.harisw.springexpensetracker.domain.expense.ExpenseCategory;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    private CreateExpenseService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new CreateExpenseService(repository);
        user = new User(1L, UUID.randomUUID(), "test@example.com", Role.USER, Instant.now());
    }

    @Test
    void create_shouldReturnSavedExpense() {
        // given
        CreateExpenseCommand command = new CreateExpenseCommand(ExpenseCategory.FOOD, "Lunch at restaurant",
                new BigDecimal("25.50"), LocalDate.of(2024, 1, 15));

        Expense savedExpense =
                new Expense(1L, user.id(), UUID.randomUUID(), ExpenseCategory.FOOD, "Lunch at restaurant",
                        new Money(new BigDecimal("25.50")), LocalDate.of(2024, 1, 15), Instant.now());

        when(repository.save(any(Expense.class))).thenReturn(savedExpense);

        // when
        Expense result = service.create(command, user);

        // then
        assertEquals(savedExpense, result);
    }

    @Test
    void create_shouldPassCorrectExpenseToRepository() {
        // given
        CreateExpenseCommand command = new CreateExpenseCommand(ExpenseCategory.TRANSPORT, "Bus ticket",
                new BigDecimal("2.00"), LocalDate.of(2024, 2, 20));

        // when
        service.create(command, user);

        // then
        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(repository).save(captor.capture());

        Expense captured = captor.getValue();
        assertNull(captured.id());
        assertEquals(user.id(), captured.userId());
        assertNotNull(captured.publicId());
        assertEquals(ExpenseCategory.TRANSPORT, captured.category());
        assertEquals("Bus ticket", captured.description());
        assertEquals(new BigDecimal("2.00"), captured.amount().amount());
        assertEquals(LocalDate.of(2024, 2, 20), captured.date());
        assertNotNull(captured.createdAt());
    }

    @Test
    void create_shouldGenerateUniquePublicId() {
        // given
        CreateExpenseCommand command = new CreateExpenseCommand(ExpenseCategory.ENTERTAINMENT, "Movie ticket",
                new BigDecimal("15.00"), LocalDate.now());

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

        CreateExpenseCommand command = new CreateExpenseCommand(ExpenseCategory.UTILITY, "Electric bill",
                new BigDecimal("100.00"), LocalDate.now());

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
