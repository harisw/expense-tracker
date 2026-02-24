package com.harisw.springexpensetracker.infrastructure.persistence.expense;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseJpaEntity, Long> {

    Optional<ExpenseJpaEntity> findByPublicId(UUID publicId);

    Optional<ExpenseJpaEntity> findByPublicIdAndUserId(UUID publicId, Long userId);

    List<ExpenseJpaEntity> findByUserId(Long userId);

    void deleteByPublicId(UUID publicId);
}
