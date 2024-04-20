package com.example.backend.controllers;

import com.example.backend.dtos.requests.ChangePasswordRequest;
import com.example.backend.dtos.requests.ForgetPasswordRequest;
import com.example.backend.dtos.requests.LoginRequest;
import com.example.backend.dtos.requests.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface AuthController {
    ResponseEntity<?> registerUser(SignupRequest signupRequest, BindingResult bindingResult);
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest, BindingResult bindingResult);
    ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest, BindingResult bindingResult);
    ResponseEntity<?> forgetPassword(ForgetPasswordRequest forgetPasswordRequest, BindingResult bindingResult);
}
