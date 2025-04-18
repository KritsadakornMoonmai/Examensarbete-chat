package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.ChatRoomDto;
import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDto;
import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDtoMin;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MessageService messageService;

    protected ChatRoomMemberDto getChatMemberDto(ChatRoomMember chatRoomMember) {
        return ChatRoomMemberDto.builder()
                .id(chatRoomMember.getId())
                .joinedAt(chatRoomMember.getJoinedAt())
                .userDtoMin(userService.getUserDtoMin(chatRoomMember.getUser()))
                .chatRoomDtoMin(chatRoomService.getChatRoomDtoMin(chatRoomMember.getChatRoom()))
                .messageDtoMins(chatRoomMember.getMessages().stream().map(message -> messageService.getMessageDtoMin(message)).toList())
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

}
