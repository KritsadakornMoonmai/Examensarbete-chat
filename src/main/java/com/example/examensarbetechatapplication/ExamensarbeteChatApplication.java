package com.example.examensarbetechatapplication;

import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RequiredArgsConstructor
@RestController
public class ExamensarbeteChatApplication {

    @RequestMapping("/")
    public String greetings() {
        return "Hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(ExamensarbeteChatApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner runner(UserRepository userRepo,
                                    UserRelationshipRepository userRelatonRepo,
                                    UserInfoRepository userInfoRepo,
                                    MessageRepository messageRepo,
                                    ChatRoomRepository chatRoomRepo,
                                    ChatRoomMemberRepository chatRoomMemRepo) {
        return (args) -> {

            User user = new User("FirstUser", "abc123", "user123@email.com");

            userRepo.save(user);

        };
    }*/

}
