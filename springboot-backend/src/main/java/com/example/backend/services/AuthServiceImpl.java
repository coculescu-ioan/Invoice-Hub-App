package com.example.backend.services;

import com.example.backend.dtos.requests.ChangePasswordRequest;
import com.example.backend.dtos.requests.ForgetPasswordRequest;
import com.example.backend.dtos.requests.LoginRequest;
import com.example.backend.dtos.requests.SignupRequest;
import com.example.backend.enums.UserRoleEnum;
import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.utilities.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    public ResponseEntity<?> registerUser(SignupRequest signupRequest) {
        List<String> body = new ArrayList<>();
        if (userService.existsByUsername(signupRequest.getUsername())) {
            body.add("Username is already taken!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            body.add("Email is already registered!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());

        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));

        user.setRole(UserRoleEnum.USER);
        userService.createUser(user);

        body.add("User registered successfully");
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElse(null);

        List<String> body = new ArrayList<>();

        if (user == null) {
            body.add("Invalid username");
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }
        else if(! new BCryptPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword())) {
            body.add("Invalid password");
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtils.generateToken(user.getUsername());

        body.add("User authenticated successfully");
        return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(body);
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByUsername(changePasswordRequest.getUsername()).orElse(null);

        if (user == null || ! new BCryptPasswordEncoder().matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            return new ResponseEntity<>("Invalid username/email or current password!", HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(new BCryptPasswordEncoder().encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        // BUSINESS LOGIC : email sending mechanism
        return new ResponseEntity<>("Password reset instructions sent to the provided email", HttpStatus.OK);
    }
}
