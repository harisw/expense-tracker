package com.harisw.springexpensetracker.application.envelope.service;

import com.harisw.springexpensetracker.application.envelope.dto.command.UpdateEnvelopeCommand;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class UpdateEnvelopeService {
    private final EnvelopeRepository repository;

    public UpdateEnvelopeService(EnvelopeRepository repository) {
        this.repository = repository;
    }

    public Envelope update(UpdateEnvelopeCommand cmd, User user) {
        Envelope existing = repository.findByPublicIdAndUserId(cmd.envelopePublicId(), user.id())
                .orElseThrow(() -> new EnvelopeNotFoundException(cmd.envelopePublicId()));
        Envelope updated = new Envelope(existing.id(), existing.userId(), existing.parentId(),
                existing.publicId(), cmd.name(),
                existing.isTemplate(),
                new Money(cmd.budget()),
                Objects.requireNonNullElse(cmd.canNotify(), false),
                existing.createdAt());
        return repository.save(updated);
    }
}
