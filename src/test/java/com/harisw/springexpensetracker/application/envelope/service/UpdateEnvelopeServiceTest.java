package com.harisw.springexpensetracker.application.envelope.service;

import com.harisw.springexpensetracker.application.envelope.dto.command.UpdateEnvelopeCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateEnvelopeServiceTest {

    @Mock
    private EnvelopeRepository repository;

    private UpdateEnvelopeService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new UpdateEnvelopeService(repository);
        user = new User(1L, "test@example.com", "Test User", UUID.randomUUID(), Instant.now());
    }

    @Test
    void update_shouldReturnUpdatedEnvelope() {
        // given
        UUID publicId = UUID.randomUUID();
        Envelope existing = envelope(1L, publicId, 5L);
        Envelope savedResult = envelope(1L, publicId, 5L);
        UpdateEnvelopeCommand cmd = new UpdateEnvelopeCommand(publicId, "Updated Name",
                new BigDecimal("750.00"), true);

        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.of(existing));
        when(repository.save(any(Envelope.class))).thenReturn(savedResult);

        // when
        Envelope result = service.update(cmd, user);

        // then
        assertEquals(savedResult, result);
    }

    @Test
    void update_shouldPreserveImmutableFields() {
        // given
        UUID publicId = UUID.randomUUID();
        Long parentId = 5L;
        Instant originalCreatedAt = Instant.parse("2024-01-01T00:00:00Z");
        Envelope existing = new Envelope(1L, user.id(), parentId, publicId, "Old Name",
                true, new Money(new BigDecimal("200.00")), false, originalCreatedAt);

        UpdateEnvelopeCommand cmd = new UpdateEnvelopeCommand(publicId, "New Name",
                new BigDecimal("999.00"), true);

        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.of(existing));

        // when
        service.update(cmd, user);

        // then
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(repository).save(captor.capture());

        Envelope captured = captor.getValue();
        assertEquals(existing.id(), captured.id());
        assertEquals(existing.userId(), captured.userId());
        assertEquals(existing.publicId(), captured.publicId());
        assertEquals(existing.parentId(), captured.parentId());
        assertEquals(existing.isTemplate(), captured.isTemplate());
        assertEquals(originalCreatedAt, captured.createdAt());
    }

    @Test
    void update_shouldApplyMutableFields() {
        // given
        UUID publicId = UUID.randomUUID();
        Envelope existing = envelope(1L, publicId, null);
        UpdateEnvelopeCommand cmd = new UpdateEnvelopeCommand(publicId, "New Name",
                new BigDecimal("350.00"), true);

        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.of(existing));

        // when
        service.update(cmd, user);

        // then
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(repository).save(captor.capture());

        Envelope captured = captor.getValue();
        assertEquals("New Name", captured.name());
        assertEquals(0, new BigDecimal("350.00").compareTo(captured.budget().amount()));
        assertTrue(captured.canNotify());
    }

    @Test
    void update_shouldThrowEnvelopeNotFoundExceptionWhenNotFound() {
        // given
        UUID publicId = UUID.randomUUID();
        when(repository.findByPublicIdAndUserId(publicId, user.id())).thenReturn(Optional.empty());

        UpdateEnvelopeCommand cmd = new UpdateEnvelopeCommand(publicId, "Name",
                new BigDecimal("100.00"), false);

        // when & then
        EnvelopeNotFoundException exception = assertThrows(EnvelopeNotFoundException.class,
                () -> service.update(cmd, user));

        assertEquals(publicId, exception.getPublicId());
        verify(repository, never()).save(any());
    }

    private Envelope envelope(Long id, UUID publicId, Long parentId) {
        return new Envelope(id, user.id(), parentId, publicId, "Test Envelope",
                false, new Money(new BigDecimal("100.00")), false, Instant.now());
    }
}
