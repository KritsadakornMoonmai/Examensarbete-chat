package com.example.examensarbetechatapplication;

import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class ExamensarbeteChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamensarbeteChatApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(UserRepository userRepo) {
        return (args) -> {

            User user = new User("FirstUser", "abc123", "user123@email.com");

            userRepo.save(user);

        };
    }

}
