package com.harisw.springexpensetracker.domain.auth;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user, String passwordHash);

    String getPasswordHashByEmail(String email);

    Optional<User> findByPublicId(UUID publicId);

    Optional<User> findByEmail(String email);

    boolean deleteByPublicId(UUID publicId);

    void deleteAll();
}
