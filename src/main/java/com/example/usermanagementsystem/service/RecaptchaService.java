package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.dto.RecaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(RecaptchaService.class);

    @Value("${google.recaptcha.secret-key}")
    private String recaptchaSecret;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate;

    public RecaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean validateRecaptcha(String recaptchaResponse) {
        String url = RECAPTCHA_VERIFY_URL + "?secret=" + recaptchaSecret + "&response=" + recaptchaResponse;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<RecaptchaResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    RecaptchaResponse.class
            );
            RecaptchaResponse response = responseEntity.getBody();
            return response != null && response.isSuccess();
        } catch (Exception e) {
            logger.error("Error verifying captcha", e);
            return false;
        }
    }
}
