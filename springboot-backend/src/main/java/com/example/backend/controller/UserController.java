package com.example.backend.controller;

import com.example.backend.models.User;

import java.util.List;

public interface UserController {
    List<User> getAllUsers();
    User getUserById(long id);
    User createUser(User user);
    User updateUser(long id, User user);
    void deleteUser(long id);
}
