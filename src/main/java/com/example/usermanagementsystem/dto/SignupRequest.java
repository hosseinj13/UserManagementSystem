package com.example.usermanagementsystem.dto;


import com.example.usermanagementsystem.util.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@PasswordMatches
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter

public class SignupRequest {

    @NotBlank(message = "Username is mandatory")
    String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^\\+98[0-9]{10}$|^09[0-9]{9}$", message = "Invalid phone number format")
    String phoneNumber;

    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{6,}$",
            message = "Password must be at least 6 characters long, include an uppercase letter, a lowercase letter, a digit, and a special character")
    String password;

    @NotBlank(message = "Confirm Password is mandatory")
    String confirmPassword;

    @NotBlank(message = "Captcha response is mandatory")
    String captchaResponse;
}
