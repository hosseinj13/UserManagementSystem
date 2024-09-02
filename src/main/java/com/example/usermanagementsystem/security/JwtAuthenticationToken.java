package com.example.usermanagementsystem.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@Getter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String jwtToken;

    public JwtAuthenticationToken(Authentication authentication, String jwtToken) {
        super(authentication.getPrincipal(), authentication.getCredentials(), authentication.getAuthorities());
        this.jwtToken = jwtToken;
    }

}
