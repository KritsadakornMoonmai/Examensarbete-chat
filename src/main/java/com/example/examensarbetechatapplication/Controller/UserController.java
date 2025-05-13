package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.DTO.UserInfoDtoMin;
import com.example.examensarbetechatapplication.Model.UserRole;
import com.example.examensarbetechatapplication.Repository.UserRoleRepository;
import com.example.examensarbetechatapplication.Service.UserInfoService;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
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
    public @ResponseBody UserDto getUserById(@RequestParam UUID id) {
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

    @PostMapping("/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String email,
                             Model model) {

        System.out.println("Creating user...");
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

            System.out.println("...Creating user done");
            return "redirect:/user/login";
        }
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
