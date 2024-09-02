package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.dto.LoginRequest;
import com.example.usermanagementsystem.dto.SignupRequest;
import com.example.usermanagementsystem.exception.UserNotFoundException;
import com.example.usermanagementsystem.mapper.UserMapper;
import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.repository.UserRepository;
import com.example.usermanagementsystem.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RecaptchaService recaptchaService;
    private final EmailVerificationService emailVerificationService;

    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, RecaptchaService recaptchaService, EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.recaptchaService = recaptchaService;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    public void registerUser(SignupRequest signupRequest) {
        logger.info("Registering user with username: {}", signupRequest.getUsername());

        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            logger.error("Passwords do not match for username: {}", signupRequest.getUsername());
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            logger.error("Username already exists: {}", signupRequest.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            logger.error("Email already exists: {}", signupRequest.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            logger.error("Phone number already exists: {}", signupRequest.getPhoneNumber());
            throw new IllegalArgumentException("Phone number already exists");
        }

        if (!recaptchaService.validateRecaptcha(signupRequest.getCaptchaResponse())) {
            logger.error("Invalid ReCAPTCHA for username: {}", signupRequest.getUsername());
            throw new IllegalArgumentException("Invalid ReCAPTCHA");
        }

        User user = UserMapper.INSTANCE.toEntity(signupRequest);
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        userRepository.save(user);

        // Send verification email
       // emailVerificationService.sendVerificationEmail(user.getEmail());
        logger.info("User registered successfully with username: {}", signupRequest.getUsername());

    }

    public Map<String, Object> authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticating user with identifier: {}", loginRequest.getIdentifier());

        if (!recaptchaService.validateRecaptcha(loginRequest.getCaptchaResponse())) {
            logger.error("Invalid ReCAPTCHA for identifier: {}", loginRequest.getIdentifier());
            return createErrorResponse("Invalid ReCAPTCHA");
        }

        // Find the user
        User user = userRepository.findByUsername(loginRequest.getIdentifier())
                .orElseGet(() -> userRepository.findByEmail(loginRequest.getIdentifier())
                        .orElseGet(() -> userRepository.findByPhoneNumber(loginRequest.getIdentifier())
                                .orElseThrow(() -> new UserNotFoundException("User not found"))));

        // Check password correctness
        if (!new BCryptPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword())) {
            logger.error("Invalid password for identifier: {}", loginRequest.getIdentifier());
            return createErrorResponse("Invalid password");
        }

//        if (!user.isEnabled()) {
//            logger.error("User not verified for identifier: {}", loginRequest.getIdentifier());
//            return createErrorResponse("User not verified");
//        }

        // Generate JWT token
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtTokenProvider.generateToken(authentication);

        // Return response in JSON format
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", jwtToken);
        response.put("message", "Logged in successfully");

        logger.info("User authenticated successfully with identifier: {}", loginRequest.getIdentifier());
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return errorResponse;
    }
}
