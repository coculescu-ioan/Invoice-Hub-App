package com.example.backend.services;

import com.example.backend.dtos.requests.LoginRequest;
import com.example.backend.dtos.requests.SignupRequest;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> registerUser(SignupRequest signupRequest) {
        return null;
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        return null;
    }
}
