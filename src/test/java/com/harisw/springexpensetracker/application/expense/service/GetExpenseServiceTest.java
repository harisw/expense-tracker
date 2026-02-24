package com.harisw.springexpensetracker.application.expense.service;

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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    private GetExpenseService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new GetExpenseService(repository);
        user = new User(1L, UUID.randomUUID(), "test@example.com", Role.USER, Instant.now());
    }

    @Test
    void get_shouldReturnExpenseWhenFound() {
        // given
        UUID publicId = UUID.randomUUID();
        Expense expense = new Expense(1L, user.id(), publicId, ExpenseCategory.FOOD, "Lunch",
                new Money(new BigDecimal("20.00")), LocalDate.now(), Instant.now());

        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.of(expense));

        // when
        Expense result = service.get(publicId, user);

        // then
        assertEquals(expense, result);
        verify(repository).findByPublicIdAndUserId(publicId, user.id());
    }

    @Test
    void get_shouldThrowExpenseNotFoundExceptionWhenNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.empty());

        // when & then
        ExpenseNotFoundException exception = assertThrows(ExpenseNotFoundException.class,
                () -> service.get(publicId, user));

        assertEquals(publicId, exception.getPublicId());
    }

    @Test
    void getAll_shouldReturnAllExpenses() {
        // given
        List<Expense> expenses = List.of(
                new Expense(1L, user.id(), UUID.randomUUID(), ExpenseCategory.FOOD, "Lunch",
                        new Money(new BigDecimal("20.00")), LocalDate.now(), Instant.now()),
                new Expense(2L, user.id(), UUID.randomUUID(), ExpenseCategory.TRANSPORT, "Bus",
                        new Money(new BigDecimal("5.00")), LocalDate.now(), Instant.now()));

        when(repository.findAll()).thenReturn(expenses);

        // when
        List<Expense> result = service.getAll();

        // then
        assertEquals(2, result.size());
        assertEquals(expenses, result);
        verify(repository).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyListWhenNoExpenses() {
        // given
        when(repository.findAll()).thenReturn(List.of());

        // when
        List<Expense> result = service.getAll();

        // then
        assertTrue(result.isEmpty());
    }
}
