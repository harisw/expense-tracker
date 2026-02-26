package com.harisw.springexpensetracker.application.envelope.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetEnvelopeService {
    private final EnvelopeRepository repository;

    public GetEnvelopeService(EnvelopeRepository repository) {
        this.repository = repository;
    }

    public Envelope get(UUID publicId, User user) {
        return repository.findByPublicIdAndUserId(publicId, user.id())
                .orElseThrow(() -> new EnvelopeNotFoundException(publicId));
    }

    public List<Envelope> getAll(User user) {
        return repository.findByUserId(user.id());
    }
}
