package com.harisw.springexpensetracker.infrastructure.persistence.envelope;

import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.domain.envelope.EnvelopeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EnvelopeRepositoryImpl implements EnvelopeRepository {
    private final EnvelopeJpaRepository jpa;

    public EnvelopeRepositoryImpl(EnvelopeJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Envelope save(Envelope envelope) {
        return EnvelopeMapper.toDomain(jpa.save(EnvelopeMapper.toEntity(envelope)));
    }

    @Override
    public Optional<Envelope> findByPublicIdAndUserId(UUID publicId, Long userId) {
        return jpa.findByPublicIdAndUserId(publicId, userId).map(EnvelopeMapper::toDomain);
    }

    @Override
    public List<Envelope> findByUserId(Long userId) {
        return jpa.findByUserId(userId).stream().map(EnvelopeMapper::toDomain).toList();
    }

    @Override
    public boolean deleteByPublicIdAndUserId(UUID publicId, Long userId) {
        return jpa.deleteByPublicIdAndUserId(publicId, userId) > 0;
    }
}
