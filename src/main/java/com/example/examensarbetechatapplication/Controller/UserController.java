package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.RelationshipStatus;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Model.UserRole;
import com.example.examensarbetechatapplication.Repository.UserRoleRepository;
import com.example.examensarbetechatapplication.Service.UserInfoService;
import com.example.examensarbetechatapplication.Service.UserRelationshipService;
import com.example.examensarbetechatapplication.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserRelationshipService userRelationshipService;

    @GetMapping("/lists")
    public @ResponseBody List<UserDto> getAllUsers() {
        return userService.getAllUser();
    }

    @GetMapping("/")
    public @ResponseBody UserDto getUserById(@RequestParam UUID id) {
        return userService.getUserDtoById(id);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<UserDto> getUserByName(@RequestParam String query, @RequestParam String username) {
        System.out.println("From: " + username);
        List<UserDto> userDtos = userService.getUserDtosByUsername(query)
                .stream()
                .filter(userDto -> !Objects.equals(username, userDto.getUsername()))
                .toList();
        System.out.println("Searched: "+userDtos.get(0).getUsername());

        return userDtos;
    }

    @PostMapping("/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String email,
                             Model model) {

        if (userService.findIfUserOrEmailExist(username, email)) {
            model.addAttribute("RegisterMessage", "Cannot create account due to information already exist");
            return "registerForm";
        } else {
            String bcryptPassword = new BCryptPasswordEncoder().encode(password);
            UserDto userDto = new UserDto();
            userDto.setUsername(username);
            userDto.setPassword(bcryptPassword);
            userDto.setEmail(email);
            userDto.setEnable(true);
            userDto.setUserRole(Collections.singleton(userService.getUserRole("User")));

            UserInfoDtoMin userInfoDtoMin = new UserInfoDtoMin();
            UserDtoMin userDtoMin = new UserDtoMin();
            userDtoMin.setUsername(userDto.getUsername());
            userDtoMin.setEmail(userDto.getEmail());

            userDto.setUserInfoDtoMin(userInfoDtoMin);

            userService.createUser(userDto);
            //userInfoService.createUserInfo(userInfoDto);

            return "redirect:/user/login";
        }
    }

    @PostMapping("/send_request")
    public String sendingFriendRequest(@RequestBody Map<String, String> payload) {
        String senderUsername = payload.get("senderUsername");
        String receiverUsername = payload.get("receiverUsername");
        Optional<UserDto> sender = Optional.ofNullable(userService.getUserDtoByUsername(senderUsername));
        Optional<UserDto> receiver = Optional.ofNullable(userService.getUserDtoByUsername(receiverUsername));

        if (sender.isEmpty() || receiver.isEmpty()) {
            return null;
        }
        UserDtoMin senderDtoMin = UserDtoMin.builder()
                .id(sender.get().getId())
                .username(sender.get().getUsername())
                .email(sender.get().getEmail())
                .build();

        UserDtoMin receiverDtoMin = UserDtoMin.builder()
                .id(receiver.get().getId())
                .username(receiver.get().getUsername())
                .email(receiver.get().getEmail())
                .build();

        UserRelationshipDto userRelationshipDto;

        if (userRelationshipService.findUserRelationShipIsExist(sender.get().getUsername(), receiver.get().getUsername())) {

            userRelationshipDto = userRelationshipService.findRelationshipByUserAndFriend(sender.get().getUsername(), receiver.get().getUsername());

            if (userRelationshipDto.getStatus() == RelationshipStatus.ACCEPTED) {
                return "redirect:/user/login/success";
            } else {
                userRelationshipDto.setStatus(RelationshipStatus.PENDING);
                userRelationshipService.saveUserRelationship(List.of(userRelationshipDto));
                return "redirect:/user/login/success";
            }
        } else {

            userRelationshipDto = new UserRelationshipDto();

            userRelationshipDto.setUser(senderDtoMin);
            userRelationshipDto.setFriend(receiverDtoMin);
            userRelationshipDto.setRelatedAt(LocalDateTime.now());
            userRelationshipDto.setStatus(RelationshipStatus.PENDING);


            sender.get().setRelationshipInitiatedDtos(List.of(userRelationshipDto));
            receiver.get().setRelationshipReceivedDtos(List.of(userRelationshipDto));

            userService.createUser(sender.get());
            userService.createUser(receiver.get());
            userRelationshipService.saveUserRelationship(List.of(userRelationshipDto));
            System.out.println("FriendRequest completed");
        }
        return "redirect:/user/login/success";
    }

    @PostMapping("/send-request/accept")
    public String friendRequestAccepted(@RequestParam String senderUsername, @RequestParam String receiverUsername, @RequestParam boolean isAccepted) {

        UserRelationshipDto userRelationshipDto;

        if (userRelationshipService.findUserRelationShipIsExist(senderUsername, receiverUsername) && isAccepted) {
            userRelationshipDto = userRelationshipService.findRelationshipByUserAndFriend(senderUsername, receiverUsername);
            userRelationshipDto.setStatus(RelationshipStatus.ACCEPTED);

        } else if (userRelationshipService.findUserRelationShipIsExist(senderUsername, receiverUsername) && !isAccepted) {
            userRelationshipDto = userRelationshipService.findRelationshipByUserAndFriend(senderUsername, receiverUsername);
            userRelationshipDto.setStatus(RelationshipStatus.REJECTED);

        } else {
            return "redirect:/user/login/success";
        }

        userRelationshipService.saveUserRelationship(List.of(userRelationshipDto));
        return "redirect:/user/login/success";
    }

    @GetMapping("/edit/{username}")
    public String getUserInfoByUsername(Model model, @PathVariable String username) {
        UserDtoMin userDto = userService.getUserDtoMinByUsername(username);
        UserInfoDto userInfoDto = userInfoService.getUserInfoDtoById(userService.getUserDtoById(userDto.getId())
                .getUserInfoDtoMin()
                .getId());
        model.addAttribute("user", userDto);
        model.addAttribute("userInfo", userInfoDto);
        return "userInfo.html";
    }

    @PostMapping("/edit/{username}")
    public String editUserInfo(@PathVariable String username,
                               @RequestParam String email,
                               @RequestParam String fullName,
                               @RequestParam int age,
                               @RequestParam String telephoneNumber,
                               @RequestParam("profileImage") MultipartFile file,
                               Model model) {

        try {
            byte[] imageBytes = file.getBytes();
            UserDto getUserDto = userService.getUserDtoByUsername(username);
            UserInfoDto getUserInfoDto = userInfoService.getUserInfoByUserDto(getUserDto);
            getUserDto.setEmail(email);
            getUserInfoDto.setFullName(fullName);
            getUserInfoDto.setAge(age);
            getUserInfoDto.setTelephoneNumber(telephoneNumber);
            getUserInfoDto.setProfileImage(imageBytes);

            userService.createUser(getUserDto);
            userInfoService.createUserInfo(getUserInfoDto);

            model.addAttribute("user", getUserDto);
            model.addAttribute("userInfo", getUserInfoDto);

            return "redirect:/user/login/success";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/edit/image/{username}")
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable String username) {
        UserDtoMin userDto = userService.getUserDtoMinByUsername(username);
        UserInfoDto userInfoDto = userInfoService.getUserInfoDtoById(userService.getUserDtoById(userDto.getId())
                .getUserInfoDtoMin()
                .getId());

        byte[] imageBytes = userInfoDto.getProfileImage();

        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.notFound().build(); // Or serve a default image
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // or detect dynamically
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }



    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String username) {
        UserDto getUserDto = userService.getUserDtoByUsername(username);
        userService.deleteUser(getUserDto);

        return "deleteConfirm.html";
    }

}
