package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {


    private final ChatRoomRepository chatRoomRepo;
    private final ChatRoomMemberService chatRoomMemberService;
    private final MessageService messageService;

    protected ChatRoomDto getChatRoomDto(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .createAt(chatRoom.getCreateAt())
                .name(chatRoom.getName())
                .chatRoomMemberDtoMins(chatRoom.getChatRoomMembers().stream().map(member -> ChatRoomMemberDtoMin.builder()
                        .id(member.getId())
                        .joinedAt(member.getJoinedAt())
                        .roles(member.getRoles())
                        .build())
                        .toList())
                .messageDtoMins(chatRoom.getMessages().stream().map(message -> MessageDtoMin.builder()
                        .id(message.getId())
                        .contents(message.getContents())
                        .time(message.getTime())
                        .build()).toList())
                .build();
    }

    protected ChatRoomDtoMin getChatRoomDtoMin(ChatRoom chatRoom) {
        return ChatRoomDtoMin.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .createAt(chatRoom.getCreateAt())
                .build();
    }


    public ChatRoomDtoMin getChatRoomDtoMiniById(long id) {
        ChatRoom chatRoom = chatRoomRepo.getReferenceById(id);
        return getChatRoomDtoMin(chatRoom);
    }

    public void saveChatRoom(ChatRoomDto chatRoomDto) {
        ChatRoom newChatRoom = ChatRoom.builder()
                .id(chatRoomDto.getId())
                .name(chatRoomDto.getName())
                .createAt(chatRoomDto.getCreateAt())
                .chatRoomMembers(chatRoomDto.getChatRoomMemberDtoMins()
                        .stream()
                        .map(chatRoomMember -> chatRoomMemberService.getChatRoomMemberById(chatRoomMember.getId()))
                        .toList())
                .messages(chatRoomDto.getMessageDtoMins()
                        .stream()
                        .map(messageDtoMin -> messageService.getMessageById(messageDtoMin.getId()))
                        .toList())
                .build();

        chatRoomRepo.save(newChatRoom);
    }
}
