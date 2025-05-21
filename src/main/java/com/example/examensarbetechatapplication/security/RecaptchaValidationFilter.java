package com.example.examensarbetechatapplication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecaptchaValidationFilter extends OncePerRequestFilter {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("/perform-login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String recaptchaResponse = request.getParameter("g-recaptcha-response");

            if (!isRecaptchaValid(recaptchaResponse)) {
                response.sendRedirect("/user/login/?error=captcha");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRecaptchaValid(String responseToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", responseToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> googleResponse = restTemplate.postForEntity(VERIFY_URL, request, Map.class);

        Map body = googleResponse.getBody();
        return body != null && Boolean.TRUE.equals(body.get("success"));
    }
}