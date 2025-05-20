package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.ChatRoomTypes;
import com.example.examensarbetechatapplication.Model.RelationshipStatus;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Service.ChatRoomMemberService;
import com.example.examensarbetechatapplication.Service.ChatRoomService;
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
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class LogInController {

    final private UserService userService;
    final private UserInfoService userInfoService;
    final private ChatRoomService chatRoomService;
    final private ChatRoomMemberService chatRoomMemberService;

    @GetMapping("/login/")
    public String userLogin() {
        return "login";
    }

    @GetMapping("/register")
    private String register() {
        return "registerForm";
    }

    @GetMapping("/login/success")
    public String userSuccessfulLogIn(@RequestParam(value = "error", required = false) String error, Model model, Principal principal) {

        try {
            String fetchUsername = principal.getName();
            Optional<UserDto> userDto = Optional.ofNullable(userService.getUserDtoByUsername(fetchUsername));
            if (userDto.isEmpty()) {
                model.addAttribute("statusMessage", "error");
                model.addAttribute("status", "Username or password is incorrect");
                return "redirect:/user/login/";
            } else {
                UserInfoDto userInfoDto = userInfoService.getUserInfoDtoById(userService.getUserDtoById(userDto.get().getId())
                        .getUserInfoDtoMin()
                        .getId());

                List<UserRelationshipDto> allAcceptedRelationships = Stream.concat(
                                userDto.get().getRelationshipInitiatedDtos().stream(),
                                userDto.get().getRelationshipReceivedDtos().stream()
                        ).filter(rel -> rel.getStatus() == RelationshipStatus.ACCEPTED)
                        .toList();


                List<UserDtoMin> actualFriends = allAcceptedRelationships.stream()
                        .map(rel -> {

                            if (userDto.get().getId().equals(rel.getUser().getId())) {
                                return rel.getFriend();
                            } else {
                                return rel.getUser();
                            }
                        })
                        .distinct()
                        .toList();

                List<UserRelationshipDto> RequestReceived = userDto.get().getRelationshipReceivedDtos()
                        .stream()
                        .filter(userRelationshipDto -> userRelationshipDto.getStatus() == RelationshipStatus.PENDING).toList();

                List<ChatRoomMemberDto> chatRoomMemberDtos = chatRoomMemberService.getChatRoomMemberDtosByUserId(userDto.get().getId());

                List<ChatRoomDto> chatRoomDtos = chatRoomMemberDtos
                        .stream()
                        .map(chatRoomMemberDto -> chatRoomService.getChatRoomDtoById(chatRoomMemberDto.getChatRoomDtoMin().getId()))
                        .toList();

                List<ChatRoomDto> filteredChatRoom = chatRoomDtos.stream().filter(chatRoomDto -> chatRoomDto.getChatRoomTypes() == ChatRoomTypes.GROUP).toList();


                //model.addAttribute("message", "success");
                model.addAttribute("user", userDto.get());
                model.addAttribute("friendList", actualFriends);
                model.addAttribute("groupChatList", filteredChatRoom);
                model.addAttribute("userInfo", userInfoDto);
                model.addAttribute("username", userDto.get().getUsername());
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
