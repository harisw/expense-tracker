package com.harisw.springexpensetracker.infrastructure.persistence.envelope;

import com.harisw.springexpensetracker.infrastructure.persistence.auth.UserJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "envelopes")
@EntityListeners(AuditingEntityListener.class)
public class EnvelopeJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",
            nullable = false
    )
    private UserJpaEntity user;

    @ManyToOne
    @JoinColumn(name = "envelope_id")
    private EnvelopeJpaEntity parentEnvelope;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;
    private String name;
    private Boolean isTemplate;
    private BigDecimal budget;
    private Boolean canNotify;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnvelopeJpaEntity getParentEnvelope() {
        return parentEnvelope;
    }

    public void setParentEnvelope(
            EnvelopeJpaEntity parentEnvelope) {
        this.parentEnvelope = parentEnvelope;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(Boolean template) {
        isTemplate = template;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Boolean getCanNotify() {
        return canNotify;
    }

    public void setCanNotify(Boolean canNotify) {
        this.canNotify = canNotify;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
