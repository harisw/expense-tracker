package com.harisw.springexpensetracker.infrastructure.persistence.envelope;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvelopeJpaRepository extends JpaRepository<EnvelopeJpaEntity, Long> {

    Optional<EnvelopeJpaEntity> findByPublicIdAndUserId(UUID publicId, Long userId);

    List<EnvelopeJpaEntity> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM EnvelopeJpaEntity e WHERE e.publicId = :publicId AND e.user.id = :userId")
    int deleteByPublicIdAndUserId(@Param("publicId") UUID publicId, @Param("userId") Long userId);
}
