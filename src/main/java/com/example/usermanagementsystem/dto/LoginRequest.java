package com.example.usermanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class LoginRequest {

    @NotBlank(message = "Identifier is mandatory")
    String identifier;

    @NotBlank(message = "Password is mandatory")
    String password;

    @NotBlank(message = "Captcha response is mandatory")
    String captchaResponse;
}
