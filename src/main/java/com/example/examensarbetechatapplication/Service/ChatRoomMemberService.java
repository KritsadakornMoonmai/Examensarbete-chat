package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.*;
import com.example.examensarbetechatapplication.Repository.ChatRoomMemberRepository;
import com.example.examensarbetechatapplication.Repository.ChatRoomRepository;
import com.example.examensarbetechatapplication.Repository.MessageRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {

    private final UserRepository userRepo;
    private final ChatRoomRepository chatRoomRepo;
    private final MessageRepository messageRepo;
    private final ChatRoomMemberRepository chatRoomMemberRepo;

    protected ChatRoomMemberDto getChatMemberDto(ChatRoomMember chatRoomMember) {
        return ChatRoomMemberDto.builder()
                .id(chatRoomMember.getId())
                .joinedAt(chatRoomMember.getJoinedAt())
                .userDtoMin(UserDtoMin.builder()
                        .id(chatRoomMember.getUser().getId())
                        .username(chatRoomMember.getUser().getUsername())
                        .email(chatRoomMember.getUser().getEmail())
                        .build())
                .chatRoomDtoMin(ChatRoomDtoMin.builder()
                        .id(chatRoomMember.getChatRoom().getId())
                        .name(chatRoomMember.getChatRoom().getName())
                        .createAt(chatRoomMember.getChatRoom().getCreateAt())
                        .build())
                .messageDtoMins(chatRoomMember.getMessages()
                        .stream()
                        .map(message -> MessageDtoMin.builder()
                                .id(message.getId())
                                .contents(message.getContents())
                                .time(message.getTime())
                                .build())
                .toList())
                .roles(chatRoomMember.getRoles())
                .build();
    }

    protected ChatRoomMemberDtoMin getChatMemberDtoMin(ChatRoomMember chatRoomMember) {

        return ChatRoomMemberDtoMin.builder()
                .id(chatRoomMember.getId())
                .joinedAt(chatRoomMember.getJoinedAt())
                .memberName(chatRoomMember.getUser().getUsername())
                .roles(chatRoomMember.getRoles())
                .build();
    }

    public ChatRoomMember getChatRoomMemberFromDto(ChatRoomMemberDto chatRoomMemberDto) {
        List<Message> messageList = chatRoomMemberDto.getMessageDtoMins()
                .stream()
                .map(messageDtoMin -> messageRepo.getReferenceById(messageDtoMin.getId())).toList();


        return ChatRoomMember.builder()
                .id(chatRoomMemberDto.getId())
                .joinedAt(chatRoomMemberDto.getJoinedAt())
                .user(userRepo.getReferenceById(chatRoomMemberDto.getUserDtoMin().getId()))
                .messages(messageList)
                .roles(chatRoomMemberDto.getRoles())
                .build();
    }

    protected ChatRoomMember getChatRoomMemberById(long id) {
        return  chatRoomMemberRepo.getReferenceById(id);
    }

    public ChatRoomMemberDtoMin getChatRoomMemberDtoMiniById(long id) {
        return getChatMemberDtoMin(chatRoomMemberRepo.getReferenceById(id));
    }

    public ChatRoomMemberDto getChatRoomMemberDtoById(long id) {
        return getChatMemberDto(chatRoomMemberRepo.getReferenceById(id));
    }


    protected ChatRoomMember getChatRoomMemberFromDtoMini(ChatRoomMemberDtoMin chatRoomMemberDtoMin) {
        return chatRoomMemberRepo.getReferenceById(chatRoomMemberDtoMin.getId());
    }

    public ChatRoomMemberDto createChatRoomMember(UserDtoMin userDtoMin, Roles roles) {
        LocalDateTime currentTime = LocalDateTime.now();
        ChatRoomMemberDto chatRoomMember = new ChatRoomMemberDto();

        chatRoomMember.setJoinedAt(currentTime);
        chatRoomMember.setUserDtoMin(userDtoMin);
        chatRoomMember.setRoles(roles);

        return chatRoomMember;
    }

    public List<ChatRoomMemberDto> getChatRoomMemberDtosByUserId(UUID userId) {

        return chatRoomMemberRepo.findChatRoomMembersByUserId(userId)
                .stream()
                .map(this::getChatMemberDto)
                .toList();
    }

    public ChatRoomMemberDto getMemberByUserAndChatRoom(Long chatRoomId, UUID userId) {
        Optional<ChatRoomMember> getChatMem = Optional.ofNullable(chatRoomMemberRepo.findChatRoomMemberByChatRoom_IdAndUser_Id(chatRoomId, userId));
        return getChatMem.map(this::getChatMemberDto).orElse(null);


    }

}
