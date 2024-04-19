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
    //@Size(min = 6, max = 20, message = "Username must be between 6 and 20 characters")
    public String username;
    @NotBlank(message = "Current password is required")
    //@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    public String currentPassword;
    @NotBlank(message = "New password is required")
    public String newPassword;

}
