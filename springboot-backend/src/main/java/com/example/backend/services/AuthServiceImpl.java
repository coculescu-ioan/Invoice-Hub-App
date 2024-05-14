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

import java.util.Objects;

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
        if (userService.existsByUsername(signupRequest.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>("Email is already registered!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());

        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));

        user.setRole(UserRoleEnum.USER);
        userService.createUser(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElse(null);

        if (user == null || ! new BCryptPasswordEncoder().matches(user.getPassword(), loginRequest.getPassword())) {
            return new ResponseEntity<>("Invalid username/email or password!", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtils.generateToken(user.getUsername());

        return ResponseEntity.ok().header("Authorization", "Bearer " + token).body("User authenticated successfully");
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByUsername(changePasswordRequest.getUsername()).orElse(null);

        if (user == null || ! new BCryptPasswordEncoder().matches(user.getPassword(), changePasswordRequest.getCurrentPassword())) {
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
