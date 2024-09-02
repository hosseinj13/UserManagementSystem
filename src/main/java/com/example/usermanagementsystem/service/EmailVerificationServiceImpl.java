package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.model.VerificationToken;
import com.example.usermanagementsystem.repository.UserRepository;
import com.example.usermanagementsystem.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final JavaMailSender emailSender;
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Value("${app.email-verification-url}")
    private String emailVerificationUrl;

    @Autowired
    public EmailVerificationServiceImpl(JavaMailSender emailSender, VerificationTokenRepository tokenRepository, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void sendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.isVerified()) {
            throw new IllegalArgumentException("User already verified");
        }

        String token = generateVerificationToken(user);
        String verificationUrl = emailVerificationUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verify your email address");
        message.setText("Please verify your email by clicking the following link: " + verificationUrl);

        emailSender.send(message);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // Token expires in 24 hours
        tokenRepository.save(verificationToken);
    }

    @Transactional
    @Override
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Token has expired
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken); // Optionally remove the used token

        return true;
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isVerified()) {
            throw new IllegalArgumentException("User already verified");
        }

        sendVerificationEmail(email); // Note: Pass email, not user object
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        Optional<VerificationToken> existingToken = tokenRepository.findByUser(user);

        // Optionally delete any existing tokens for the user
        existingToken.ifPresent(tokenRepository::delete);

        return token;
    }
}
