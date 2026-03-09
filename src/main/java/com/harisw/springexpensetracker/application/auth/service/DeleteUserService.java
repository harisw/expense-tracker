package com.harisw.springexpensetracker.application.auth.service;

import com.harisw.springexpensetracker.domain.auth.UserRepository;
import com.harisw.springexpensetracker.domain.auth.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteUserService {
    private final UserRepository repository;

    public DeleteUserService(UserRepository repository) {
        this.repository = repository;
    }

    public void delete(UUID publicId) {
        boolean deleted = repository.deleteByPublicId(publicId);
        if (!deleted) {
            throw new UserNotFoundException();
        }
    }
}
