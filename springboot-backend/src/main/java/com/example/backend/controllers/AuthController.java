package com.example.backend.controllers;

import com.example.backend.dtos.requests.ChangePasswordRequest;
import com.example.backend.dtos.requests.ForgetPasswordRequest;
import com.example.backend.dtos.requests.LoginRequest;
import com.example.backend.dtos.requests.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface AuthController {
    ResponseEntity<?> registerUser(SignupRequest signupRequest);
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
    ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest);
    ResponseEntity<?> forgetPassword(ForgetPasswordRequest forgetPasswordRequest);
}
