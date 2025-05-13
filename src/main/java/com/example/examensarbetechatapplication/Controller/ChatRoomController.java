package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.ChatRoomTypes;
import com.example.examensarbetechatapplication.Model.Message;
import com.example.examensarbetechatapplication.Model.Roles;
import com.example.examensarbetechatapplication.Service.ChatRoomMemberService;
import com.example.examensarbetechatapplication.Service.ChatRoomService;
import com.example.examensarbetechatapplication.Service.MessageService;
import com.example.examensarbetechatapplication.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatRoomMemberService chatRoomMemberService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/fetch")
    public String getChatRoomByUsers(
            @RequestParam UUID userId,
            @RequestParam UUID friendId,
            @RequestParam String type,
            Model model) {

        ChatRoomTypes chatRoomTypes;
        if (Objects.equals(type, "PRIVATE")) {
            chatRoomTypes = ChatRoomTypes.PRIVATE;
        } else {
            chatRoomTypes = ChatRoomTypes.GROUP;
        }

        Optional<ChatRoomDto> chatRoomDto = chatRoomService.getChatRoomByUserAndFriend(userId, friendId, chatRoomTypes);
        UserDtoMin userDto = userService.getUserDtoMinById(userId);
        List<ChatRoomMemberDto> chatRoomMemberDtos;
        ChatRoomMemberDto sender;
        ChatRoomDto getFinalChatRoom;
        List<MessageDto> messageList;

        if (chatRoomDto.isPresent()) {
            getFinalChatRoom = chatRoomDto.get();
            chatRoomMemberDtos = chatRoomDto
                    .get()
                    .getChatRoomMemberDtoMins()
                    .stream()
                    .map(chatRoomMemberDtoMin -> chatRoomMemberService.getChatRoomMemberDtoById(chatRoomMemberDtoMin.getId()))
                    .toList();

            sender = chatRoomMemberDtos
                    .stream()
                    .filter(chatRoomMemberDto -> userDto.getId() == chatRoomMemberDto.getUserDtoMin().getId())
                    .findFirst()
                    .orElse(null);

            messageList = messageService.getAllMessage().stream()
                    .filter(messageDto -> messageDto.getChatRoomDtoMin().getId() == chatRoomDto.get().getId())
                    .sorted(Comparator.comparing(MessageDto::getTime))
                    .toList();

        } else {
            messageList = new ArrayList<>();
            UserDtoMin friendDto = userService.getUserDtoMinById(friendId);

            ChatRoomMemberDto chatRoomMemberDto1 = chatRoomMemberService.createChatRoomMember(userDto, Roles.MEMBER);
            ChatRoomMemberDto chatRoomMemberDto2 = chatRoomMemberService.createChatRoomMember(friendDto, Roles.MEMBER);


            getFinalChatRoom = chatRoomService.createChatRoom(List.of(chatRoomMemberDto1, chatRoomMemberDto2), chatRoomTypes);
            ChatRoomDtoMin chatRoomDtoMin = ChatRoomDtoMin.builder()
                    .id(getFinalChatRoom.getId())
                    .name(getFinalChatRoom.getName())
                    .createAt(getFinalChatRoom.getCreateAt()).build();

            chatRoomMemberDto1.setMessageDtoMins(new ArrayList<>());
            chatRoomMemberDto2.setMessageDtoMins(new ArrayList<>());

            getFinalChatRoom.setMessageDtoMins(new ArrayList<>());

            chatRoomMemberDtos = List.of(chatRoomMemberDto1, chatRoomMemberDto2);
            chatRoomService.saveChatRoom(getFinalChatRoom, List.of(chatRoomMemberService.getChatRoomMemberFromDto(chatRoomMemberDto1),  chatRoomMemberService.getChatRoomMemberFromDto(chatRoomMemberDto2)));
            sender = chatRoomMemberDtos
                    .stream()
                    .filter(chatRoomMemberDto -> userDto.getId() == chatRoomMemberDto.getUserDtoMin().getId())
                    .findFirst()
                    .orElse(null);


        }

        System.out.println(getFinalChatRoom.getChatRoomMemberDtoMins().get(1).getMemberName());
        model.addAttribute("user", userDto);
        model.addAttribute("sender", Objects.requireNonNull(sender));
        model.addAttribute("chatRoom", getFinalChatRoom);
        model.addAttribute("messages", messageList);

        return "chat :: content";
    }

    /*@PostMapping("/create")
    public String createChatRoom(@RequestParam long userId,
                                 @RequestParam long friendId,
                                 @RequestParam String type,
                                 Model model) {
        UserDtoMin userDto = userService.getUserDtoMinById(userId);
        UserDtoMin friendDto = userService.getUserDtoMinById(friendId);

        ChatRoomMemberDto chatRoomMemberDto1 = chatRoomMemberService.createChatRoomMember(userDto, Roles.MEMBER);
        ChatRoomMemberDto chatRoomMemberDto2 = chatRoomMemberService.createChatRoomMember(friendDto, Roles.MEMBER);

        ChatRoomTypes chatRoomTypes = ChatRoomTypes.valueOf(type);

        ChatRoomDto chatRoomDto = chatRoomService.createChatRoom(List.of(chatRoomMemberDto1, chatRoomMemberDto2), chatRoomTypes);
        ChatRoomDtoMin chatRoomDtoMin = ChatRoomDtoMin.builder()
                .id(chatRoomDto.getId())
                .name(chatRoomDto.getName())
                .createAt(chatRoomDto.getCreateAt()).build();

        chatRoomMemberDto1.setChatRoomDtoMin(chatRoomDtoMin);
        chatRoomMemberDto2.setChatRoomDtoMin(chatRoomDtoMin);

        chatRoomMemberService.saveChatRoomMember(chatRoomMemberDto1);
        chatRoomMemberService.saveChatRoomMember(chatRoomMemberDto2);

        chatRoomService.saveChatRoom(chatRoomDto);

        model.addAttribute("chatRoom", chatRoomDto);
        model.addAttribute("user", userDto);
        return "chat";
    }*/
}

