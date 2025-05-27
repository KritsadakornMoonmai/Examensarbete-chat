package com.example.examensarbetechatapplication;

import com.example.examensarbetechatapplication.Model.*;
import com.example.examensarbetechatapplication.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ExamensarbeteChatApplication {

    @GetMapping("/")
    public String greetings() {
        return "Hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(ExamensarbeteChatApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner runner(UserRepository userRepo,
                                    UserRelationshipRepository userRelatonRepo,
                                    UserInfoRepository userInfoRepo,
                                    MessageRepository messageRepo,
                                    ChatRoomRepository chatRoomRepo,
                                    ChatRoomMemberRepository chatRoomMemRepo,
                                    UserRoleRepository userRoleRepo) {
        return (args) -> {

            String passwordEncoder = new BCryptPasswordEncoder().encode("abc123");
            String passwordEncoder2 = new BCryptPasswordEncoder().encode("asd456");

            UserRole userRole1 = new UserRole();
            UserRole userRole2 = new UserRole();
            userRole1.setRole("Admin");
            userRole2.setRole("User");

            if (!userRoleRepo.existsUserRoleByRole(userRole1.getRole()) && !userRoleRepo.existsUserRoleByRole(userRole2.getRole())) {
                userRoleRepo.save(userRole1);
                userRoleRepo.save(userRole2);
            } else {
                System.out.println("Role: " + userRole1.getRole() + ", " + userRole2.getRole() + "are already exists.");
            }


            User user = new User("FirstUser",passwordEncoder, "user123@email.com");
            User user2 = new User("User2", passwordEncoder2, "userqwe@email.com");

            if (!userRepo.existsByUsernameOrEmail(user.getUsername(), user.getEmail()) && !userRepo.existsByUsernameOrEmail(user2.getUsername(), user2.getEmail())) {
                UserInfo newUserInfo = new UserInfo();
                UserInfo newUserInfo2 = new UserInfo();
                newUserInfo.setUser(user);
                newUserInfo2.setUser(user2);





                user.setRoles(Collections.singleton(userRole1));
                user.setEnable(true);

                user2.setRoles(Collections.singleton(userRole1));
                user2.setEnable(true);

                UserRelationship userRelationship = new UserRelationship();
                userRelationship.setUser(user);
                userRelationship.setFriend(user2);
                userRelationship.setRelatedAt(LocalDateTime.now());
                userRelationship.setStatus(RelationshipStatus.ACCEPTED);


                user.setRelationshipInitiated(List.of(userRelationship));
                user2.setRelationshipReceived(List.of(userRelationship));

                userRepo.saveAll(List.of(user, user2));
                userInfoRepo.saveAll(List.of(newUserInfo, newUserInfo2));
                userRelatonRepo.saveAll(List.of(userRelationship));
            } else {
                System.out.println("User with username '" + user.getUsername() + ", " + user2.getUsername() + "' already exists. Skipping.");
            }
        };
    }

}
