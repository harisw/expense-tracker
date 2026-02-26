package com.harisw.springexpensetracker.infrastructure.persistence.auth;

import com.harisw.springexpensetracker.domain.auth.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserJpaEntity toEntity(User u) {
        UserJpaEntity jpa = new UserJpaEntity();
        jpa.setId(u.id());
        jpa.setEmail(u.email());
        jpa.setName(u.name());
        jpa.setPublicId(u.publicId());
        jpa.setCreatedAt(u.createdAt());
        return jpa;
    }

    public static User toDomain(UserJpaEntity u) {
        return new User(u.getId(), u.getEmail(), u.getName(), u.getPublicId(), u.getCreatedAt());
    }
}
