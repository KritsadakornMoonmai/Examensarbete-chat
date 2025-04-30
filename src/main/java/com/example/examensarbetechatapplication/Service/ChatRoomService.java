package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.Message;
import com.example.examensarbetechatapplication.Repository.ChatRoomMemberRepository;
import com.example.examensarbetechatapplication.Repository.ChatRoomRepository;
import com.example.examensarbetechatapplication.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {


    final private ChatRoomRepository chatRoomRepo;
    final private ChatRoomMemberRepository chatRoomMemberRepo;
    final private MessageRepository messageRepo;

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

    public ChatRoom getChatRoomFromDto(ChatRoomDto chatRoomDto) {
        ChatRoom chatRoom = ChatRoom.builder()
                .id(chatRoomDto.getId())
                .name(chatRoomDto.getName())
                .createAt(chatRoomDto.getCreateAt())
                .build();

        List<ChatRoomMember> chatRoomMemberList = chatRoomDto.getChatRoomMemberDtoMins()
                .stream()
                .map(chatRoomMemberDtoMin -> {
                    ChatRoomMember chatRoomMember = chatRoomMemberRepo.getReferenceById(chatRoomMemberDtoMin.getId());
                    chatRoomMember.setChatRoom(chatRoom);
                    return chatRoomMember;
                })
                .toList();

        List<Message> messageList = chatRoomDto.getMessageDtoMins()
                .stream()
                .map(messageDtoMin -> {
                    Message message = messageRepo.getReferenceById(messageDtoMin.getId());
                    message.setChatRoom(null);
                    return message;
                })
                .toList();

        chatRoom.setChatRoomMembers(chatRoomMemberList);
        chatRoom.setMessages(messageList);

        return chatRoom;
    }

    public void saveChatRoom(ChatRoomDto chatRoomDto) {
        ChatRoom newChatRoom = getChatRoomFromDto(chatRoomDto);
        System.out.println("newChatRoom saving " + newChatRoom.getName());
        chatRoomRepo.save(newChatRoom);
    }
}
