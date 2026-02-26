package com.harisw.springexpensetracker.domain.envelope;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvelopeRepository {
    Envelope save(Envelope envelope);

    Optional<Envelope> findByPublicIdAndUserId(UUID publicId, Long userId);

    List<Envelope> findByUserId(Long userId);

    boolean deleteByPublicIdAndUserId(UUID publicId, Long userId);
}
