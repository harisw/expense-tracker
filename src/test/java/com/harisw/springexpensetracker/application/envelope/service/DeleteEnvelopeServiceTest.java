package com.harisw.springexpensetracker.application.envelope.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
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
class DeleteEnvelopeServiceTest {

    @Mock
    private EnvelopeRepository repository;

    private DeleteEnvelopeService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new DeleteEnvelopeService(repository);
        user = new User(1L, "test@example.com", "Test User", UUID.randomUUID(), Instant.now());
    }

    @Test
    void delete_shouldSucceedWhenEnvelopeExists() {
        // given
        UUID publicId = UUID.randomUUID();
        when(repository.deleteByPublicIdAndUserId(publicId, user.id())).thenReturn(true);

        // when
        service.delete(publicId, user);

        // then
        verify(repository).deleteByPublicIdAndUserId(publicId, user.id());
    }

    @Test
    void delete_shouldThrowEnvelopeNotFoundExceptionWhenNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        when(repository.deleteByPublicIdAndUserId(publicId, user.id())).thenReturn(false);

        // when & then
        EnvelopeNotFoundException exception = assertThrows(EnvelopeNotFoundException.class,
                () -> service.delete(publicId, user));

        assertEquals(publicId, exception.getPublicId());
    }
}
