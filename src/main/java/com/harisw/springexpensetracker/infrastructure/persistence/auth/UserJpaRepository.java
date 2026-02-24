package com.harisw.springexpensetracker.infrastructure.persistence.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByPublicId(UUID publicId);

    Optional<UserJpaEntity> findByEmail(String email);

    void deleteByPublicId(UUID publicId);
}
