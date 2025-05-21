package com.example.examensarbetechatapplication.security;

import com.example.examensarbetechatapplication.Repository.UserInfoRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import com.example.examensarbetechatapplication.Repository.UserRoleRepository;
import com.example.examensarbetechatapplication.Service.ChatRoomMemberService;
import com.example.examensarbetechatapplication.Service.UserInfoService;
import com.example.examensarbetechatapplication.Service.UserRelationshipService;
import com.example.examensarbetechatapplication.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    final private UserRepository userRepo;
    final private UserInfoRepository userInfoRepo;
    final private UserRoleRepository userRoleRepo;
    final private UserInfoService userInfoService;
    final private UserRelationshipService userRelationshipService;
    final private ChatRoomMemberService chatRoomMemberService;


    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService(userRepo, userInfoRepo, userRoleRepo, userInfoService, userRelationshipService, chatRoomMemberService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(recaptchaValidationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/",  "/js/**", "/css/**", "/images/**", "/login/**", "/user/login", "/logout","/queues/**", "/user/register", "/api/user/create", "/reset-password").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/user/login/")
                        .loginProcessingUrl("/perform-login")
                        .defaultSuccessUrl("/user/login/success", true)
                        .permitAll()

                )
                .logout((logout) -> logout
                        .logoutUrl("/user/perform-logout")
                        .logoutSuccessUrl("/user/login/") // Redirect to the custom login page after logout
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    @Profile("!test")
    public RecaptchaValidationFilter recaptchaValidationFilter() {
        return new RecaptchaValidationFilter();
    }
}


