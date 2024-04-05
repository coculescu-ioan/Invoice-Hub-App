package com.example.backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Username / email is required")
    @Size(min = 6)
    private String usernameOrEmail;
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30)
    private String password;
}
