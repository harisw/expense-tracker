package com.harisw.springexpensetracker.application.expense.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.expense.ExpenseNotFoundException;
import com.harisw.springexpensetracker.domain.expense.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    private DeleteExpenseService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new DeleteExpenseService(repository);
        user = new User(1L, "test@example.com", "Test User", UUID.randomUUID(), Instant.now());
    }

    @Test
    void delete_shouldSucceedWhenExpenseExists() {
        // given
        UUID publicId = UUID.randomUUID();
        when(repository.deleteByPublicIdAndUserId(publicId, user.id())).thenReturn(true);

        // when
        service.delete(publicId, user);

        // then
        verify(repository).deleteByPublicIdAndUserId(publicId, user.id());
    }

    @Test
    void delete_shouldThrowExpenseNotFoundExceptionWhenNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        when(repository.deleteByPublicIdAndUserId(publicId, user.id())).thenReturn(false);

        // when & then
        ExpenseNotFoundException exception = assertThrows(ExpenseNotFoundException.class,
                () -> service.delete(publicId, user));

        assertEquals(publicId, exception.getPublicId());
    }
}
