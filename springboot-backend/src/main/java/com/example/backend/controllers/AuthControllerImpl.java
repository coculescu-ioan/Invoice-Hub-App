package com.example.backend.controllers;

import com.example.backend.dtos.requests.ChangePasswordRequest;
import com.example.backend.dtos.requests.ForgetPasswordRequest;
import com.example.backend.dtos.requests.LoginRequest;
import com.example.backend.dtos.requests.SignupRequest;
import com.example.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthControllerImpl implements AuthController{

    private final AuthService authService;

    @Autowired
    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        return authService.registerUser(signupRequest);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @Override
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return authService.changePassword(changePasswordRequest);
    }

    @Override
    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        return null;
    }
}
