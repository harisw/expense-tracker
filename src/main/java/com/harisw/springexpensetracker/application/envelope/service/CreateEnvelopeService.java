package com.harisw.springexpensetracker.application.envelope.service;

import com.harisw.springexpensetracker.application.envelope.dto.command.CreateEnvelopeCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class CreateEnvelopeService {
    private final EnvelopeRepository repository;

    public CreateEnvelopeService(EnvelopeRepository repository) {
        this.repository = repository;
    }

    public Envelope create(CreateEnvelopeCommand cmd, User user) {
        Long parentId = null;
        if (cmd.envelopeParentPublicId() != null) {
            Envelope parent = repository.findByPublicIdAndUserId(cmd.envelopeParentPublicId(), user.id())
                    .orElseThrow(() -> new EnvelopeNotFoundException(cmd.envelopeParentPublicId()));
            parentId = parent.id();
        }
        Envelope envelope = new Envelope(null, user.id(), parentId, UUID.randomUUID(), cmd.name(),
                Objects.requireNonNullElse(cmd.isTemplate(), false),
                new Money(cmd.budget()),
                Objects.requireNonNullElse(cmd.canNotify(), false),
                Instant.now());
        return repository.save(envelope);
    }
}
