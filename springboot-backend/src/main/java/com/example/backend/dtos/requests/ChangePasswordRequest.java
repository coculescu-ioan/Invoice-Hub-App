package com.example.backend.dtos.requests;

import com.example.backend.annotations.ValidNewPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidNewPassword
public class ChangePasswordRequest {

    @NotBlank(message = "Username is required")
    public String username;

    @NotBlank(message = "Current password is required")
    public String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min=8, max=20, message="New password length must be between 8 and 20 characters")
    public String newPassword;

}
