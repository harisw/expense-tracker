package com.harisw.springexpensetracker.application.envelope.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeNotFoundException;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteEnvelopeService {
    private final EnvelopeRepository repository;

    public DeleteEnvelopeService(EnvelopeRepository repository) {
        this.repository = repository;
    }

    public void delete(UUID publicId, User user) {
        boolean deleted = repository.deleteByPublicIdAndUserId(publicId, user.id());
        if (!deleted) {
            throw new EnvelopeNotFoundException(publicId);
        }
    }
}
