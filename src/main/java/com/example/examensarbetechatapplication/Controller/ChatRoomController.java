package com.example.examensarbetechatapplication.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    @GetMapping("/")
    public String getChatRoom(RequestParam chatRoomId, RequestParam userId) {
        return "Chatroom";
    }

    @PostMapping("/")
    public String createChatRoom(@RequestParam long userId,
                                 @RequestParam long friendId) {
        return "Chatroom";
    }
}

