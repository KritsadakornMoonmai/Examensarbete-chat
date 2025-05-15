package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.DTO.UserRelationshipDto;
import com.example.examensarbetechatapplication.Model.RelationshipStatus;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Service.UserInfoService;
import com.example.examensarbetechatapplication.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class LogInController {

    final private UserService userService;
    final private UserInfoService userInfoService;

    @GetMapping("/login/")
    public String userLogin() {
        return "login";
    }

    @GetMapping("/register")
    private String register() {
        return "registerForm";
    }

    @GetMapping("/login/success")
    public String userSuccessfulLogIn(Model model, Principal principal) {

        try {
            String fetchUsername = principal.getName();
            UserDto userDto = userService.getUserDtoByUsername(fetchUsername);
            UserInfoDto userInfoDto = userInfoService.getUserInfoDtoById(userService.getUserDtoById(userDto.getId())
                    .getUserInfoDtoMin()
                    .getId());

            List<UserRelationshipDto> allAcceptedRelationships = Stream.concat(
                            userDto.getRelationshipInitiatedDtos().stream(),
                            userDto.getRelationshipReceivedDtos().stream()
                    ).filter(rel -> rel.getStatus() == RelationshipStatus.ACCEPTED)
                    .toList();


            List<UserDtoMin> actualFriends = allAcceptedRelationships.stream()
                    .map(rel -> {

                        if (userDto.getId().equals(rel.getUser().getId())) {
                            return rel.getFriend();
                        } else {
                            return rel.getUser();
                        }
                    })
                    .distinct()
                    .toList();

            List<UserRelationshipDto> RequestReceived = userDto.getRelationshipReceivedDtos()
                    .stream()
                    .filter(userRelationshipDto -> userRelationshipDto.getStatus() == RelationshipStatus.PENDING).toList();

            for (int i = 0; i < RequestReceived.size(); i++) {
                System.out.println("CurrentUser: " + userDto.getUsername());
                System.out.println("Receiver: " + RequestReceived.get(i).getUser().getUsername());
            }



            if (userDto == null) {
                model.addAttribute("message", "error");
                model.addAttribute("status", "UserNotFound");
                return "redirect:/user/login/";
            } else {
                model.addAttribute("message", "success");
                model.addAttribute("user", userDto);
                model.addAttribute("friendList", actualFriends);
                model.addAttribute("userInfo", userInfoDto);
                model.addAttribute("username", userDto.getUsername());
                model.addAttribute("friendRequest", RequestReceived);
                return "main";
            }
        } catch (Exception e) {
            model.addAttribute("message", "error");
            model.addAttribute("status", e);
            return "redirect:/user/login/";
        }

    }

    @GetMapping("/perform-logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/user/login/";
    }
}
