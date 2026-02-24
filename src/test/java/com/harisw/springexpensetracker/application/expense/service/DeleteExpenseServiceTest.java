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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    private DeleteExpenseService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new DeleteExpenseService(repository);
        user = new User(1L, UUID.randomUUID(), "test@example.com", Role.USER, Instant.now());
    }

    @Test
    void delete_shouldDeleteExpenseWhenExists() {
        // given
        UUID publicId = UUID.randomUUID();
        Expense expense = new Expense(1L, user.id(), publicId, ExpenseCategory.FOOD, "Lunch",
                new Money(new BigDecimal("20.00")), LocalDate.now(), Instant.now());

        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.of(expense));

        // when
        service.delete(publicId, user);

        // then
        verify(repository).findByPublicIdAndUserId(publicId, user.id());
        verify(repository).deleteByPublicId(publicId);
    }

    @Test
    void delete_shouldThrowExpenseNotFoundExceptionWhenNotExists() {
        // given
        UUID publicId = UUID.randomUUID();
        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.empty());

        // when & then
        ExpenseNotFoundException exception = assertThrows(ExpenseNotFoundException.class,
                () -> service.delete(publicId, user));

        assertEquals(publicId, exception.getPublicId());
        verify(repository, never()).deleteByPublicId(any());
    }
}
