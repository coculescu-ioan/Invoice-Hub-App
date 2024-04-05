package com.example.backend.services;

import com.example.backend.dtos.requests.LoginRequest;
import com.example.backend.dtos.requests.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> registerUser(SignupRequest signupRequest);
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
    //ResponseEntity<?> changePassword();
    //ResponseEntity<?> forgetPassword();
}
