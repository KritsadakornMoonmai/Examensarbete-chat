package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.ChatRoomDto;
import com.example.examensarbetechatapplication.DTO.ChatRoomDtoMin;
import com.example.examensarbetechatapplication.Model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    @Autowired
    private ChatRoomMemberService chatRoomMemberService;

    @Autowired
    private MessageService messageService;

    protected ChatRoomDto getChatRoomDto(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .createAt(chatRoom.getCreateAt())
                .name(chatRoom.getName())
                .chatRoomMemberDtoMins(chatRoom.getChatRoomMembers().stream().map(member -> chatRoomMemberService.getChatMemberDtoMin(member)).toList())
                .messageDtoMins(chatRoom.getMessages().stream().map(message -> messageService.getMessageDtoMin(message)).toList())
                .build();
    }

    protected ChatRoomDtoMin getChatRoomDtoMin(ChatRoom chatRoom) {
        return ChatRoomDtoMin.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .createAt(chatRoom.getCreateAt())
                .build();
    }
}
