package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.dto.LoginRequest;
import com.example.usermanagementsystem.exception.AdminNotFoundException;
import com.example.usermanagementsystem.model.Admin;
import com.example.usermanagementsystem.repository.AdminRepository;
import com.example.usermanagementsystem.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public AdminService(AdminRepository adminRepository, JwtTokenProvider jwtTokenProvider) {
        this.adminRepository = adminRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Map<String, Object> authenticateAdmin(LoginRequest loginRequest) {

        logger.info("Authenticating admin with identifier: {}", loginRequest.getIdentifier());


        // Find the admin
        Admin admin = adminRepository.findByUsername(loginRequest.getIdentifier())
                .orElseGet(() -> adminRepository.findByUsername(loginRequest.getIdentifier())
                                .orElseThrow(() -> new AdminNotFoundException("Admin not found")));

        // Check password correctness
        if (!new BCryptPasswordEncoder().matches(loginRequest.getPassword(), admin.getPassword())) {
            logger.error("Invalid password for admin identifier: {}", loginRequest.getIdentifier());
            return createErrorResponse();
        }

        // Generate JWT token
        Authentication authentication = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtTokenProvider.generateToken(authentication);

        // Return response in JSON format
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", jwtToken);
        response.put("message", "Admin logged in successfully");

        logger.info("Admin authenticated successfully with identifier: {}", loginRequest.getIdentifier());
        return response;
    }

    private Map<String, Object> createErrorResponse() {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Invalid password");
        return errorResponse;
    }

}
