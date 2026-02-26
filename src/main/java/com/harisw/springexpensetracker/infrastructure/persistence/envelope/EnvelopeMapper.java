package com.harisw.springexpensetracker.infrastructure.persistence.envelope;

import com.harisw.springexpensetracker.domain.common.Money;
import com.harisw.springexpensetracker.domain.envelope.Envelope;
import com.harisw.springexpensetracker.infrastructure.persistence.auth.UserJpaEntity;

public final class EnvelopeMapper {
    private EnvelopeMapper() {
    }

    public static EnvelopeJpaEntity toEntity(Envelope e) {
        EnvelopeJpaEntity jpa = new EnvelopeJpaEntity();
        jpa.setId(e.id());

        UserJpaEntity userRef = new UserJpaEntity();
        userRef.setId(e.userId());
        jpa.setUser(userRef);

        if (e.parentId() != null) {
            EnvelopeJpaEntity parentRef = new EnvelopeJpaEntity();
            parentRef.setId(e.parentId());
            jpa.setParentEnvelope(parentRef);
        }

        jpa.setPublicId(e.publicId());
        jpa.setName(e.name());
        jpa.setTemplate(e.isTemplate());
        jpa.setBudget(e.budget().amount());
        jpa.setCanNotify(e.canNotify());
        jpa.setCreatedAt(e.createdAt());
        return jpa;
    }

    public static Envelope toDomain(EnvelopeJpaEntity e) {
        Long parentId = e.getParentEnvelope() != null ? e.getParentEnvelope().getId() : null;
        return new Envelope(e.getId(), e.getUser().getId(), parentId, e.getPublicId(), e.getName(),
                e.getTemplate(), new Money(e.getBudget()), e.getCanNotify(), e.getCreatedAt());
    }
}
