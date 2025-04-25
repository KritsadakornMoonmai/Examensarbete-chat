package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.Service.UserInfoService;
import com.example.examensarbetechatapplication.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/lists")
    public @ResponseBody List<UserDto> getAllUsers() {
        return userService.getAllUser();
    }

    @GetMapping("/")
    public @ResponseBody UserDto getUserById(@RequestParam long id, Model model) {
        return userService.getUserDtoById(id);
    }

    @GetMapping("/search/{userInfo_FullName}")
    public String getUserByName(@PathVariable String userInfo_FullName, Model model) {
        List<UserInfoDto> userInfoDtos = userInfoService.getUserInfoDtoByName(userInfo_FullName);
        List<UserDto> userDtos = userService.getUserByUserInfoFullName(userInfo_FullName);

        model.addAttribute(userInfoDtos);
        model.addAttribute(userDtos);

        return "page.html";
    }

    @PostMapping("/create/{username}")
    public String createUser(@PathVariable String username,
                             @RequestParam String password,
                             @RequestParam String email) {
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setPassword(password);
        userDto.setEmail(email);
        userService.createUser(userDto);

        UserInfoDto userInfoDto = new UserInfoDto();
        UserDtoMin userDtoMin = new UserDtoMin();
        userDtoMin.setUsername(userDto.getUsername());
        userDtoMin.setEmail(userDto.getEmail());

        userInfoDto.setUserDtoMin(userDtoMin);
        userInfoService.createUserInfo(userInfoDto);

        return "user";
    }

    @GetMapping("/edit/{username}")
    public String getUserInfoByUsername(Model model, @PathVariable String username) {
        UserDtoMin userDto = userService.getUserDtoMinByUsername(username);
        UserInfoDto userInfoDto = userInfoService.getUserInfoDtoById(userService.getUserDtoById(userDto.getId())
                .getUserInfoDtoMin()
                .getId());
        model.addAttribute("user", userDto);
        model.addAttribute("userInfo", userInfoDto);
        return "edit.html";
    }

    @PostMapping("/edit/{username}")
    public String editUserInfo(@PathVariable String username,
                               @RequestParam String email,
                               @RequestParam String fullName,
                               @RequestParam int age,
                               @RequestParam String telephoneNumber,
                               @RequestParam ImageIcon profileImage,
                               Model model) {
        UserDto getUserDto = userService.getUserDtoByUsername(username);
        UserInfoDto getUserInfoDto = userInfoService.getUserInfoByUserDto(getUserDto);
        getUserDto.setEmail(email);
        getUserInfoDto.setFullName(fullName);
        getUserInfoDto.setAge(age);
        getUserInfoDto.setTelephoneNumber(telephoneNumber);
        getUserInfoDto.setProfileImage(profileImage);

        model.addAttribute("user", getUserDto);
        model.addAttribute("userInfo", getUserInfoDto);

        return "info.html";
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String username) {
        UserDto getUserDto = userService.getUserDtoByUsername(username);
        userService.deleteUser(getUserDto);

        return "deleteConfirm.html";
    }

}
