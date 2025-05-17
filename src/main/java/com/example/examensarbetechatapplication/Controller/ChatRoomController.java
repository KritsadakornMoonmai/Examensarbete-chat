package com.example.examensarbetechatapplication.Controller;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.ChatRoomTypes;
import com.example.examensarbetechatapplication.Model.Roles;
import com.example.examensarbetechatapplication.Service.ChatRoomMemberService;
import com.example.examensarbetechatapplication.Service.ChatRoomService;
import com.example.examensarbetechatapplication.Service.MessageService;
import com.example.examensarbetechatapplication.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

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
        Optional<ChatRoomDto> chatRoomDto;
        System.out.println("type: " + type);
        if (Objects.equals(type, "PRIVATE")) {
            chatRoomTypes = ChatRoomTypes.PRIVATE;
            chatRoomDto = chatRoomService.getChatRoomByUserAndFriend(userId, friendId, chatRoomTypes);
        } else {
            return "redirect:/user/login/success";
        }

        UserDtoMin userDto = userService.getUserDtoMinById(userId);
        UserDtoMin friendDto = userService.getUserDtoMinById(friendId);
        List<ChatRoomMemberDto> chatRoomMemberDtos;
        ChatRoomMemberDto sender;
        ChatRoomMemberDto senderFriend;
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

            senderFriend = chatRoomMemberDtos
                    .stream()
                    .filter(chatRoomMemberDto -> friendDto.getId() == chatRoomMemberDto.getUserDtoMin().getId())
                    .findFirst()
                    .orElse(null);

            messageList = messageService.getAllMessage().stream()
                    .filter(messageDto -> messageDto.getChatRoomDtoMin().getId() == chatRoomDto.get().getId())
                    .sorted(Comparator.comparing(MessageDto::getTime))
                    .toList();

        } else {
            messageList = new ArrayList<>();

            ChatRoomMemberDto chatRoomMemberDto1 = chatRoomMemberService.createChatRoomMember(userDto, Roles.MEMBER);
            ChatRoomMemberDto chatRoomMemberDto2 = chatRoomMemberService.createChatRoomMember(friendDto, Roles.MEMBER);


            ChatRoomDto getChatRoom = chatRoomService.createChatRoom(List.of(chatRoomMemberDto1, chatRoomMemberDto2), chatRoomTypes);

            chatRoomMemberDto1.setMessageDtoMins(new ArrayList<>());
            chatRoomMemberDto2.setMessageDtoMins(new ArrayList<>());

            getChatRoom.setMessageDtoMins(new ArrayList<>());
            getFinalChatRoom =  chatRoomService.saveChatRoom(getChatRoom, List.of(chatRoomMemberService.getChatRoomMemberFromDto(chatRoomMemberDto1),  chatRoomMemberService.getChatRoomMemberFromDto(chatRoomMemberDto2)));
            chatRoomMemberDtos = getFinalChatRoom
                    .getChatRoomMemberDtoMins()
                    .stream()
                    .map(chatRoomMemberDtoMin -> chatRoomMemberService.getChatRoomMemberDtoById(chatRoomMemberDtoMin.getId()))
                    .toList();

            sender = chatRoomMemberDtos
                    .stream()
                    .filter(chatRoomMemberDto -> userDto.getId() == chatRoomMemberDto.getUserDtoMin().getId())
                    .findFirst()
                    .orElse(null);

            senderFriend = chatRoomMemberDtos
                    .stream()
                    .filter(chatRoomMemberDto -> friendDto.getId() == chatRoomMemberDto.getUserDtoMin().getId())
                    .findFirst()
                    .orElse(null);
        }

        model.addAttribute("user", userDto);
        model.addAttribute("sender", Objects.requireNonNull(sender));
        model.addAttribute("senderFriend", Objects.requireNonNull(senderFriend));
        model.addAttribute("chatRoom", getFinalChatRoom);
        model.addAttribute("messages", messageList);
        model.addAttribute("chatRoomTypes", ChatRoomTypes.class);

        return "chat :: content";
    }

    @GetMapping("/fetchGroup")
    public String getChatRoomGroup(@RequestParam Long chatRoomId, @RequestParam UUID userId, Model model) {
        System.out.println("Begin fetch group:" + chatRoomId);
        Optional<ChatRoomDto> chatRoomDto = Optional.ofNullable(chatRoomService.getChatRoomDtoById(chatRoomId));
        List<MessageDto> messageDtos;
        Optional<UserDto> userDto = Optional.ofNullable(userService.getUserDtoById(userId));
        ChatRoomMemberDto sender;
        List<ChatRoomMemberDto> chatRoomMemberDtos;

        if (chatRoomDto.isEmpty()) {
            System.out.println("chat room empty");
            model.addAttribute("errorMessage", "No chatroom found");
            return "redirect:/user/login/success";

        } else if (userDto.isEmpty()) {
            System.out.println("user empty");
            return "redirect:/user/login/success";
        } else {
            messageDtos = chatRoomDto.get().getMessageDtoMins().stream().map(messageDtoMin -> messageService.getMessageDtoById(messageDtoMin.getId())).toList();
            chatRoomMemberDtos = chatRoomDto.get()
                    .getChatRoomMemberDtoMins()
                    .stream()
                    .map(chatRoomMemberDtoMin -> chatRoomMemberService.getChatRoomMemberDtoById(chatRoomMemberDtoMin.getId()))
                    .toList();

            sender = chatRoomMemberDtos
                    .stream()
                    .filter(chatRoomMemberDto -> userDto.get().getId() == chatRoomMemberDto.getUserDtoMin().getId())
                    .findFirst()
                    .orElse(null);

            System.out.println("Fetch group chat: " + chatRoomDto.get().getName());
            System.out.println("Fetch group mem1: " + chatRoomDto.get().getChatRoomMemberDtoMins().get(0).getMemberName());
            model.addAttribute("chatRoom", chatRoomDto.get());
            model.addAttribute("messages", messageDtos);
            model.addAttribute("sender", Objects.requireNonNull(sender));
            model.addAttribute("chatRoomTypes", ChatRoomTypes.class);
            return "chat :: content";
        }
    }

    @PostMapping("/createGroup")
    public String createChatRoomGroup(@RequestParam UUID userId, @RequestParam String memberIds, @RequestParam String type, @RequestParam String roomName, Model model) {
        ChatRoomTypes chatRoomTypes;
        System.out.println("memberIds: "+memberIds);
        List<UUID> memberIdLists = new ArrayList<>(Arrays.stream(memberIds.split(","))
                .map(UUID::fromString)
                .toList());

        memberIdLists.add(userId);

        if (Objects.equals(type, "GROUP")) {
            chatRoomTypes = ChatRoomTypes.GROUP;
        } else {
            model.addAttribute("errorMessage", "Cannot create chatRoom");
            return "redirect:/user/login/success";
        }

        List<UserDtoMin> userDtoMins = new ArrayList<>();

        for (UUID memberId : memberIdLists) {
            System.out.println("memberIds in list: "+memberId);
            Optional<UserDtoMin> userDto = Optional.ofNullable(userService.getUserDtoMinById(memberId));
            if (userDto.isEmpty()) {
                model.addAttribute("errorMessage", "User does not exist");
                return "redirect:/user/login/success";
            } else {
                userDtoMins.add(userDto.get());
            }
        }

        List<ChatRoomMemberDto> chatRoomMemberDtos = userDtoMins
                .stream()
                .map(userDtoMin ->
                {
                    ChatRoomMemberDto chatRoomMemberDto;
                    if (userDtoMin.getId() == userId) {
                        chatRoomMemberDto = chatRoomMemberService.createChatRoomMember(userDtoMin, Roles.ADMIN);
                    } else {
                        chatRoomMemberDto = chatRoomMemberService.createChatRoomMember(userDtoMin, Roles.MEMBER);
                    }
                    chatRoomMemberDto.setMessageDtoMins(new ArrayList<>());
                    return chatRoomMemberDto;
                }).toList();

        ChatRoomMemberDto sender = chatRoomMemberDtos
                .stream()
                .filter(chatRoomMemberDto -> userId == chatRoomMemberDto.getUserDtoMin().getId())
                .findFirst()
                .orElse(null);


        ChatRoomDto newChatRoomDto = chatRoomService.createChatRoom(chatRoomMemberDtos, chatRoomTypes);
        newChatRoomDto.setName(roomName);
        newChatRoomDto.setMessageDtoMins(new ArrayList<>());

        ChatRoomDto finalChatRoomDto = chatRoomService.saveChatRoom(newChatRoomDto, chatRoomMemberDtos.stream().map(chatRoomMemberDto -> chatRoomMemberService.getChatRoomMemberFromDto(chatRoomMemberDto)).toList());
        model.addAttribute("chatRoomGroup", finalChatRoomDto);
        model.addAttribute("sender", Objects.requireNonNull(sender));
        return "redirect:/user/login/success";
    }

    @PostMapping("/addMemberGroup")
    public String addChatRoomMemberGroup(@RequestParam UUID userId, @RequestParam List<UUID> memberIds, @RequestParam long chatRoomId, @RequestParam String type, Model model) {
        ChatRoomTypes chatRoomTypes;
        if (Objects.equals(type, "GROUP")) {
            chatRoomTypes = ChatRoomTypes.GROUP;
        } else {
            model.addAttribute("errorMessage", "Cannot create chatRoom");
            return "redirect:/user/login/success";
        }

        List<UserDtoMin> userDtoMins = new ArrayList<>();
        for (UUID memberId : memberIds) {
            Optional<UserDtoMin> userDto = Optional.ofNullable(userService.getUserDtoMinById(memberId));
            if (userDto.isEmpty()) {
                model.addAttribute("errorMessage", "User does not exist");
                return "redirect:/user/login/success";
            } else {
                userDtoMins.add(userDto.get());
            }
        }

        List<ChatRoomMemberDto> chatRoomMemberDtos = userDtoMins
                .stream()
                .map(userDtoMin ->
                {
                    ChatRoomMemberDto chatRoomMemberDto;
                    if (userDtoMin.getId() == userId) {
                        chatRoomMemberDto = chatRoomMemberService.createChatRoomMember(userDtoMin, Roles.ADMIN);
                    } else {
                        chatRoomMemberDto = chatRoomMemberService.createChatRoomMember(userDtoMin, Roles.MEMBER);
                    }

                    return chatRoomMemberDto;
                }).toList();


        ChatRoomDto newChatRoomDto = chatRoomService.createChatRoom(chatRoomMemberDtos, chatRoomTypes);
        model.addAttribute("chatRoomGroup", newChatRoomDto);
        return "redirect:/user/login/success";
    }
}

