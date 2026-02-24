package com.harisw.springexpensetracker.domain.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user, String passwordHash);

    String getPasswordHashByEmail(String email);

    Optional<User> findByPublicId(UUID publicId);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    void deleteByPublicId(UUID publicId);
}
