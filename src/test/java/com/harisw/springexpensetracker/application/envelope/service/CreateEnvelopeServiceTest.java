package com.harisw.springexpensetracker.application.envelope.service;

import com.harisw.springexpensetracker.application.envelope.dto.command.CreateEnvelopeCommand;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
class CreateEnvelopeServiceTest {

    @Mock
    private EnvelopeRepository repository;

    private CreateEnvelopeService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new CreateEnvelopeService(repository);
        user = new User(1L, "test@example.com", "Test User", UUID.randomUUID(), Instant.now());
    }

    @Test
    void create_shouldReturnSavedEnvelope() {
        // given
        CreateEnvelopeCommand cmd = new CreateEnvelopeCommand(null, "Groceries",
                false, new BigDecimal("500.00"), false);
        Envelope saved = envelope(1L, user.id(), null);
        when(repository.save(any(Envelope.class))).thenReturn(saved);

        // when
        Envelope result = service.create(cmd, user);

        // then
        assertEquals(saved, result);
    }

    @Test
    void create_shouldPassCorrectFieldsToRepository() {
        // given
        CreateEnvelopeCommand cmd = new CreateEnvelopeCommand(null, "Transport",
                true, new BigDecimal("200.00"), true);

        // when
        service.create(cmd, user);

        // then
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(repository).save(captor.capture());

        Envelope captured = captor.getValue();
        assertNull(captured.id());
        assertEquals(user.id(), captured.userId());
        assertNull(captured.parentId());
        assertNotNull(captured.publicId());
        assertEquals("Transport", captured.name());
        assertTrue(captured.isTemplate());
        assertEquals(0, new BigDecimal("200.00").compareTo(captured.budget().amount()));
        assertTrue(captured.canNotify());
        assertNotNull(captured.createdAt());
    }

    @Test
    void create_withNullBooleans_shouldDefaultToFalse() {
        // given
        CreateEnvelopeCommand cmd = new CreateEnvelopeCommand(null, "Utilities",
                null, new BigDecimal("100.00"), null);

        // when
        service.create(cmd, user);

        // then
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(repository).save(captor.capture());

        Envelope captured = captor.getValue();
        assertFalse(captured.isTemplate());
        assertFalse(captured.canNotify());
    }

    @Test
    void create_withParentEnvelope_shouldResolveParentId() {
        // given
        UUID parentPublicId = UUID.randomUUID();
        Envelope parent = envelope(10L, user.id(), null);
        when(repository.findByPublicIdAndUserId(parentPublicId, user.id())).thenReturn(Optional.of(parent));

        CreateEnvelopeCommand cmd = new CreateEnvelopeCommand(parentPublicId, "Sub-Groceries",
                false, new BigDecimal("100.00"), false);

        // when
        service.create(cmd, user);

        // then
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(repository).save(captor.capture());
        assertEquals(parent.id(), captor.getValue().parentId());
    }

    @Test
    void create_withNonExistentParent_shouldThrowEnvelopeNotFoundException() {
        // given
        UUID nonExistentParentId = UUID.randomUUID();
        when(repository.findByPublicIdAndUserId(nonExistentParentId, user.id())).thenReturn(Optional.empty());

        CreateEnvelopeCommand cmd = new CreateEnvelopeCommand(nonExistentParentId, "Sub-Envelope",
                false, new BigDecimal("50.00"), false);

        // when & then
        EnvelopeNotFoundException exception = assertThrows(EnvelopeNotFoundException.class,
                () -> service.create(cmd, user));

        assertEquals(nonExistentParentId, exception.getPublicId());
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldGenerateUniquePublicId() {
        // given
        CreateEnvelopeCommand cmd = new CreateEnvelopeCommand(null, "Entertainment",
                false, new BigDecimal("150.00"), false);

        // when
        service.create(cmd, user);
        service.create(cmd, user);

        // then
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(repository, times(2)).save(captor.capture());

        var captured = captor.getAllValues();
        assertNotEquals(captured.get(0).publicId(), captured.get(1).publicId());
    }

    @Test
    void create_shouldSetCreatedAtToCurrentTime() {
        // given
        Instant before = Instant.now();
        CreateEnvelopeCommand cmd = new CreateEnvelopeCommand(null, "Rent",
                false, new BigDecimal("1000.00"), false);

        // when
        service.create(cmd, user);
        Instant after = Instant.now();

        // then
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(repository).save(captor.capture());

        Instant createdAt = captor.getValue().createdAt();
        assertTrue(createdAt.compareTo(before) >= 0);
        assertTrue(createdAt.compareTo(after) <= 0);
    }

    private Envelope envelope(Long id, Long userId, Long parentId) {
        return new Envelope(id, userId, parentId, UUID.randomUUID(), "Test Envelope",
                false, new Money(new BigDecimal("100.00")), false, Instant.now());
    }
}
