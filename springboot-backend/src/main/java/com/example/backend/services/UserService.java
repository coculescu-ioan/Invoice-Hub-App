package com.example.backend.services;

import com.example.backend.models.User;

import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> findByUsername(String username);
    //findByEmail
    //existsByUsername
    //existsByEmail
}
