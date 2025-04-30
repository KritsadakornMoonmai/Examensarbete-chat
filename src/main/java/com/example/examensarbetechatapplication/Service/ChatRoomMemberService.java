package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.Message;
import com.example.examensarbetechatapplication.Repository.ChatRoomMemberRepository;
import com.example.examensarbetechatapplication.Repository.ChatRoomRepository;
import com.example.examensarbetechatapplication.Repository.MessageRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .roles(chatRoomMember.getRoles())
                .build();
    }

    protected ChatRoomMember getChatRoomMemberFromDto(ChatRoomMemberDto chatRoomMemberDto) {
        List<Message> messageList = chatRoomMemberDto.getMessageDtoMins()
                .stream()
                .map(messageDtoMin -> messageRepo.getReferenceById(messageDtoMin.getId())).toList();


        return ChatRoomMember.builder()
                .id(chatRoomMemberDto.getId())
                .joinedAt(chatRoomMemberDto.getJoinedAt())
                .user(userRepo.getReferenceById(chatRoomMemberDto.getUserDtoMin().getId()))
                .chatRoom(chatRoomRepo.getReferenceById(chatRoomMemberDto.getChatRoomDtoMin().getId()))
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


    protected ChatRoomMember getChatRoomMemberFromDtoMini(ChatRoomMemberDtoMin chatRoomMemberDtoMin) {
        return chatRoomMemberRepo.getReferenceById(chatRoomMemberDtoMin.getId());
    }

}
