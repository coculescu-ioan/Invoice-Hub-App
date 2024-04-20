package com.example.backend.services;

import com.example.backend.dtos.requests.ChangePasswordRequest;
import com.example.backend.dtos.requests.ForgetPasswordRequest;
import com.example.backend.dtos.requests.LoginRequest;
import com.example.backend.dtos.requests.SignupRequest;
import com.example.backend.enums.UserRole;
import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<?> registerUser(SignupRequest signupRequest) {
        if (userService.existsByUsername(signupRequest.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>("Email is already registered!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());
        user.setRole(UserRole.USER);
        userService.createUser(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElse(null);

        // avoiding null pointer exception : Objects.equals()
        if (user == null || !Objects.equals(user.getPassword(), loginRequest.getPassword())) {
            return new ResponseEntity<>("Invalid username/email or password!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("User authenticated successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByUsername(changePasswordRequest.getUsername()).orElse(null);

        if (user == null || !Objects.equals(user.getPassword(), changePasswordRequest.getCurrentPassword())) {
            return new ResponseEntity<>("Invalid username/email or current password!", HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(changePasswordRequest.getNewPassword());
        userRepository.save(user);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        // business logic needed : email sending mechanism
        return new ResponseEntity<>("Password reset instructions sent to the provided email", HttpStatus.OK);
    }
}
