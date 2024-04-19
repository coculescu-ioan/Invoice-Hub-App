package com.example.backend.controllers;

import com.example.backend.models.User;

import java.util.List;
// might not be needed :
// controllers should be process-oriented
// rather than entity-oriented
public interface UserController {
    List<User> getAllUsers();
    User getUserById(long id);
    User createUser(User user);
    User updateUser(long id, User user);
    void deleteUser(long id);
}
