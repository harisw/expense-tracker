package com.harisw.springexpensetracker.infrastructure.persistence.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseJpaEntity, Long> {

    Optional<ExpenseJpaEntity> findByPublicId(UUID publicId);

    Optional<ExpenseJpaEntity> findByPublicIdAndEnvelopeId(UUID publicId, Long envelopeId);

    List<ExpenseJpaEntity> findByEnvelopeId(Long envelopeId);

//    List<ExpenseJpaEntity> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM ExpenseJpaEntity e WHERE e.publicId = :publicId AND e.envelope.user.id = :userId")
    int deleteByPublicIdAndUserId(@Param("publicId") UUID publicId, @Param("userId") Long userId);
}
