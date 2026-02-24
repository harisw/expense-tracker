package com.harisw.springexpensetracker.infrastructure.persistence.auth;

import com.harisw.springexpensetracker.domain.auth.AuthProvider;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.auth.UserRepository;
import com.harisw.springexpensetracker.domain.auth.exception.InvalidCredentialsException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpa;

    public UserRepositoryImpl(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public User save(User user, String passwordHash) {
        UserJpaEntity userJpaEntity = UserMapper.toEntity(user);
        userJpaEntity.setPasswordHash(passwordHash);
        userJpaEntity.setAuthProvider(AuthProvider.LOCAL);
        return UserMapper.toDomain(jpa.save(userJpaEntity));
    }

    @Override
    public String getPasswordHashByEmail(String email) {
        return jpa.findByEmail(email)
                .map(UserJpaEntity::getPasswordHash)
                .orElseThrow(InvalidCredentialsException::new);
    }

    @Override
    public Optional<User> findByPublicId(UUID publicId) {
        return jpa.findByPublicId(publicId).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpa.findAll().stream().map(UserMapper::toDomain).toList();
    }

    @Override
    public void deleteByPublicId(UUID publicId) {
        jpa.deleteByPublicId(publicId);
    }


}
