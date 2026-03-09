package com.harisw.springexpensetracker.application.auth.service;

import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.auth.UserRepository;
import com.harisw.springexpensetracker.domain.auth.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserByPublicIdService {
    private final UserRepository repository;

    public GetUserByPublicIdService(UserRepository repository) {
        this.repository = repository;
    }

    public User GetUserByPublicId(UUID publicId) {
        User user = repository.findByPublicId(publicId)
                .orElseThrow(UserNotFoundException::new);

        return user;
    }
}
