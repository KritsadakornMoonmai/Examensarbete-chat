package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.MessageDto;
import com.example.examensarbetechatapplication.DTO.MessageDtoMin;
import com.example.examensarbetechatapplication.Model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatRoomMemberService chatRoomMemberService;

    protected MessageDto getMessageDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .contents(message.getContents())
                .time(message.getTime())
                .chatRoomMemberDtoMin(chatRoomMemberService.getChatMemberDtoMin(message.getChatRoomMember()))
                .chatRoomDtoMin(chatRoomService.getChatRoomDtoMin(message.getChatRoom())).build();
    }

    protected MessageDtoMin getMessageDtoMin(Message message) {
        return MessageDtoMin.builder()
                .id(message.getId())
                .contents(message.getContents())
                .time(message.getTime())
                .build();
    }
}
