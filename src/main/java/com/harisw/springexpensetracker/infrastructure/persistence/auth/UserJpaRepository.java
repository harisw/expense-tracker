package com.harisw.springexpensetracker.infrastructure.persistence.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByPublicId(UUID publicId);

    Optional<UserJpaEntity> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM UserJpaEntity u WHERE u.publicId = :publicId")
    int deleteByPublicId(UUID publicId);

    @Modifying
    @Query("DELETE FROM UserJpaEntity")
    void deleteAll();

}
