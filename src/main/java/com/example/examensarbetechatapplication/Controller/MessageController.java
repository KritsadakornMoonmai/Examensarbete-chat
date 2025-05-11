package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.ChatRoomDtoMin;
import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDtoMin;
import com.example.examensarbetechatapplication.DTO.MessageDto;
import com.example.examensarbetechatapplication.Model.Message;
import com.example.examensarbetechatapplication.Service.ChatRoomMemberService;
import com.example.examensarbetechatapplication.Service.ChatRoomService;
import com.example.examensarbetechatapplication.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatRoomMemberService chatRoomMemberService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDto chatMessage) {

        System.out.println("sendMessage activated");
        chatMessage.setTime(LocalDateTime.now());
        messageService.saveMessage(chatMessage);

        messagingTemplate.convertAndSend(
                "/topic/chatroom/" + chatMessage.getChatRoomDtoMin().getId(), chatMessage);
    }

    @GetMapping("/")
    public String getMessage(@RequestParam long userId, @RequestParam long chatRoomId) {
        ChatRoomMemberDtoMin CRMDMini = chatRoomMemberService.getChatRoomMemberDtoMiniById(userId);
        ChatRoomDtoMin CRDMini = chatRoomService.getChatRoomDtoMiniById(chatRoomId);

        MessageDto getMessageDto = messageService.getMessageHandler(CRMDMini, CRDMini);
        return "messsage";
    }

    @PostMapping("/sent")
    public MessageDto postMessage(Model model, @RequestParam long userId, @RequestParam long chatRoomId, @RequestParam String contents) {
        MessageDto messageDto = new MessageDto();
        messageDto.setTime(LocalDateTime.now());
        messageDto.setContents(contents);
        messageDto.setChatRoomDtoMin(chatRoomService.getChatRoomDtoMiniById(chatRoomId));
        messageDto.setChatRoomMemberDtoMin(chatRoomMemberService.getChatRoomMemberDtoMiniById(userId));

        messageService.saveMessage(messageDto);
        model.addAttribute("message", messageDto);
        return messageDto;
    }
}
