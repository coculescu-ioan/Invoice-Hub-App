package com.example.backend.services;

import com.example.backend.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> getAllUsers();
    User getUserById(long id);
    User updateUser(long id, User user);
    void deleteUser(long id);
}
