package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/lists")
    public @ResponseBody List<UserDto> getAllUsers() {
        return userService.getAllUser();
    }
}
