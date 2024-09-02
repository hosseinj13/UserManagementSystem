package com.example.usermanagementsystem.controller;


import com.example.usermanagementsystem.dto.LoginRequest;
import com.example.usermanagementsystem.dto.SignupRequest;
import com.example.usermanagementsystem.service.ConcurrentAuthService;
import com.example.usermanagementsystem.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ConcurrentAuthService concurrentAuthService;
    private final EmailVerificationService emailVerificationService;

    public AuthController(ConcurrentAuthService concurrentAuthService, EmailVerificationService emailVerificationService) {
        this.concurrentAuthService = concurrentAuthService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CompletableFuture<String> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return concurrentAuthService.registerUserConcurrently(signupRequest)
                .thenApply(result -> "Registration request is being processed");
    }

    @PostMapping("/login")
    @ResponseBody
    public CompletableFuture<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        return concurrentAuthService.authenticateUserConcurrently(loginRequest);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean isVerified = emailVerificationService.verifyEmail(token);
        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody String email) {
        emailVerificationService.resendVerificationEmail(email);
        return ResponseEntity.ok("Verification email sent.");
    }
}
