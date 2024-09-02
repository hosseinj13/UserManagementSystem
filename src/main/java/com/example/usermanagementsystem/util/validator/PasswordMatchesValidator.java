package com.example.usermanagementsystem.util.validator;

import com.example.usermanagementsystem.dto.SignupRequest;
import com.example.usermanagementsystem.util.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SignupRequest> {

    @Override
    public boolean isValid(SignupRequest signupRequest, ConstraintValidatorContext context) {
        return signupRequest.getPassword().equals(signupRequest.getConfirmPassword());
    }
}
