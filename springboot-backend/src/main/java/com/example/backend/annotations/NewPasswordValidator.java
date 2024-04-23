package com.example.backend.annotations;

import com.example.backend.dtos.requests.ChangePasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class NewPasswordValidator implements ConstraintValidator<ValidNewPassword, ChangePasswordRequest> {
    @Override
    public boolean isValid(ChangePasswordRequest changePasswordRequest,
                           ConstraintValidatorContext constraintValidatorContext) {
        return !Objects.equals(changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword());
    }


}
