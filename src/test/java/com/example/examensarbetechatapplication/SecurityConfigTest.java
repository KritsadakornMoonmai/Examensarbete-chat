package com.example.examensarbetechatapplication;

import com.example.examensarbetechatapplication.security.RecaptchaValidationFilter;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SecurityConfigTest {
    @Bean
    public RecaptchaValidationFilter recaptchaValidationFilter() {
        return Mockito.mock(RecaptchaValidationFilter.class);
    }
}
