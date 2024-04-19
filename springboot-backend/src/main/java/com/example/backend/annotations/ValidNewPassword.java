package com.example.backend.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NewPasswordValidator.class)
public @interface ValidNewPassword {
    String message() default "New password cannot be the same as current password.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
