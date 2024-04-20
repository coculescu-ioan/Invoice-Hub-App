package com.example.backend.controllers;

import com.example.backend.models.User;

import java.util.List;
// MIGHT NOT BE NEEDED:
// Generally, a good practice in MVC architecture
// is for controllers to focus on managing processes
// rather than directly interacting with entities
public interface UserController {
    List<User> getAllUsers();
    User getUserById(long id);
    User createUser(User user);
    User updateUser(long id, User user);
    void deleteUser(long id);
}
