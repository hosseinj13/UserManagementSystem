package com.example.usermanagementsystem.service;

public interface EmailVerificationService {
    void sendVerificationEmail(String email);
    boolean verifyEmail(String token);
    void resendVerificationEmail(String email);
}
