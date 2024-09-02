package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.dto.LoginRequest;
import com.example.usermanagementsystem.dto.SignupRequest;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ConcurrentAuthService {

    private final AuthService authService;
    private final ExecutorService executorService;

    public ConcurrentAuthService(AuthService authService) {
        this.authService = authService;

        // Create a Thread Pool with a fixed number of Threads
        this.executorService = Executors.newFixedThreadPool(1000);
    }

    // simultaneous registration
    public CompletableFuture<Void> registerUserConcurrently(SignupRequest signupRequest) {
        return CompletableFuture.runAsync(() -> authService.registerUser(signupRequest), executorService);
    }


    // Simultaneous login
    public CompletableFuture<Map<String, Object>> authenticateUserConcurrently(LoginRequest loginRequest) {
        return CompletableFuture.supplyAsync(() -> authService.authenticateUser(loginRequest), executorService);
    }


    // Shut down the ExecutorService when closing the application
    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
