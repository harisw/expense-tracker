package com.harisw.springexpensetracker.application.envelope.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEnvelopeServiceTest {

    @Mock
    private EnvelopeRepository repository;

    private GetEnvelopeService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new GetEnvelopeService(repository);
        user = new User(1L, "test@example.com", "Test User", UUID.randomUUID(), Instant.now());
    }

    @Test
    void get_shouldReturnEnvelopeWhenFound() {
        // given
        UUID publicId = UUID.randomUUID();
        Envelope envelope = envelope(1L, publicId);
        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.of(envelope));

        // when
        Envelope result = service.get(publicId, user);

        // then
        assertEquals(envelope, result);
        verify(repository).findByPublicIdAndUserId(publicId, user.id());
    }

    @Test
    void get_shouldThrowEnvelopeNotFoundExceptionWhenNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.empty());

        // when & then
        EnvelopeNotFoundException exception = assertThrows(EnvelopeNotFoundException.class,
                () -> service.get(publicId, user));

        assertEquals(publicId, exception.getPublicId());
    }

    @Test
    void getAll_shouldReturnAllEnvelopesForUser() {
        // given
        List<Envelope> envelopes = List.of(
                envelope(1L, UUID.randomUUID()),
                envelope(2L, UUID.randomUUID()),
                envelope(3L, UUID.randomUUID()));
        when(repository.findByUserId(user.id())).thenReturn(envelopes);

        // when
        List<Envelope> result = service.getAll(user);

        // then
        assertEquals(3, result.size());
        assertEquals(envelopes, result);
        verify(repository).findByUserId(user.id());
    }

    @Test
    void getAll_shouldReturnEmptyListWhenNoEnvelopes() {
        // given
        when(repository.findByUserId(user.id())).thenReturn(List.of());

        // when
        List<Envelope> result = service.getAll(user);

        // then
        assertTrue(result.isEmpty());
    }

    private Envelope envelope(Long id, UUID publicId) {
        return new Envelope(id, user.id(), null, publicId, "Test Envelope",
                false, new Money(new BigDecimal("100.00")), false, Instant.now());
    }
}
