package com.example.usermanagementsystem.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter

public class RecaptchaResponse {

     boolean success;
     String challenge_ts;
     String hostname;
     String[] errorCodes;
}
