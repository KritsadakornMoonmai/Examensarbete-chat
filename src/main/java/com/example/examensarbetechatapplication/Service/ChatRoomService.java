package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.*;
import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.ChatRoomTypes;
import com.example.examensarbetechatapplication.Model.Message;
import com.example.examensarbetechatapplication.Repository.ChatRoomMemberRepository;
import com.example.examensarbetechatapplication.Repository.ChatRoomRepository;
import com.example.examensarbetechatapplication.Repository.MessageRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {


    final private ChatRoomRepository chatRoomRepo;
    final private ChatRoomMemberRepository chatRoomMemberRepo;
    final private MessageRepository messageRepo;
    final private UserRepository userRepo;

    protected ChatRoomDto getChatRoomDto(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .createAt(chatRoom.getCreateAt())
                .name(chatRoom.getName())
                .chatRoomTypes(chatRoom.getChatRoomTypes())
                .chatRoomMemberDtoMins(chatRoom.getChatRoomMembers().stream().map(member -> ChatRoomMemberDtoMin.builder()
                        .id(member.getId())
                        .joinedAt(member.getJoinedAt())
                        .roles(member.getRoles())
                                .memberName(member.getUser().getUsername())
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

    public ChatRoomDto getChatRoomDtoById(long id) {
        ChatRoom chatRoom = chatRoomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        return getChatRoomDto(chatRoom);
    }


    public ChatRoomDtoMin getChatRoomDtoMiniById(long id) {
        ChatRoom chatRoom = chatRoomRepo.getReferenceById(id);
        return getChatRoomDtoMin(chatRoom);
    }

    public ChatRoom getChatRoomFromDto(ChatRoomDto chatRoomDto, List<ChatRoomMember> chatRoomMember) {
        ChatRoom chatRoom = ChatRoom.builder()
                .id(chatRoomDto.getId())
                .name(chatRoomDto.getName())
                .chatRoomTypes(chatRoomDto.getChatRoomTypes())
                .createAt(chatRoomDto.getCreateAt())
                .build();


        List<Message> messageList = Optional.ofNullable(chatRoomDto.getMessageDtoMins())
                .orElse(Collections.emptyList())
                .stream()
                .map(messageDtoMin -> messageRepo.findById(messageDtoMin.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();


        chatRoom.setChatRoomMembers(chatRoomMember);
        chatRoomMember.forEach(chatRoomMember1 -> chatRoomMember1.setChatRoom(chatRoom));
        chatRoom.setMessages(messageList);

        return chatRoom;
    }

    public ChatRoomDto createChatRoom(List<ChatRoomMemberDto> chatRoomMemberList, ChatRoomTypes chatRoomTypes) {
        LocalDateTime current = LocalDateTime.now();
        ChatRoomDto chatRoom;
        List<ChatRoomMemberDtoMin> chatRoomMemberDtoMinList = new ArrayList<>();
        if (chatRoomMemberList.size() < 2) {
            return null;
        } else {
            chatRoom = new ChatRoomDto();
            StringBuilder roomName = null;
            for (int i = 0; i < chatRoomMemberList.size(); i++) {
                ChatRoomMemberDto CRMDto = chatRoomMemberList.get(i);
                ChatRoomMemberDtoMin CRMDtoMin = ChatRoomMemberDtoMin.builder()
                        .id(CRMDto.getId())
                        .roles(CRMDto.getRoles())
                        .memberName(CRMDto.getUserDtoMin().getUsername())
                        .joinedAt(CRMDto.getJoinedAt())
                        .build();
                if (i == chatRoomMemberList.size() - 1) {
                    roomName = (roomName == null ? new StringBuilder("null") : roomName).append(chatRoomMemberList.get(i).getUserDtoMin().getUsername());
                } else {
                    roomName = (roomName == null ? new StringBuilder("null") : roomName).append(chatRoomMemberList.get(i).getUserDtoMin().getUsername()).append(", ");
                }
                chatRoomMemberDtoMinList.add(CRMDtoMin);
            }
            chatRoom.setChatRoomMemberDtoMins(chatRoomMemberDtoMinList);
            chatRoom.setName(roomName != null ? roomName.toString() : null);
            chatRoom.setCreateAt(current);
            chatRoom.setChatRoomTypes(chatRoomTypes);
            return chatRoom;
        }
    }

    public ChatRoomDto saveChatRoom(ChatRoomDto chatRoomDto, List<ChatRoomMember> chatRoomMember) {
        ChatRoom newChatRoom = getChatRoomFromDto(chatRoomDto, chatRoomMember);
        System.out.println("newChatRoom saving " + newChatRoom.getName());
        return getChatRoomDto(chatRoomRepo.save(newChatRoom));
    }

    public Optional<ChatRoomDto> getChatRoomByUserAndFriend(UUID userId, UUID friendId, ChatRoomTypes chatRoomTypes) {
        List<ChatRoomMember> user1MemList = chatRoomMemberRepo.findChatRoomMembersByUserId(userId);
        List<ChatRoomMember> user2MemList = chatRoomMemberRepo.findChatRoomMembersByUserId(friendId);



        if (user1MemList.isEmpty() || user2MemList.isEmpty() || chatRoomTypes == null) {
            return Optional.empty();
        }
        List<UUID> chatRoomMemberIdList = List.of(userId, friendId);
        List<ChatRoom> chatRoomList = chatRoomRepo.findChatRoomWithExactMembersByUserIds(chatRoomMemberIdList, chatRoomMemberIdList.size()).stream()
                .filter(chatRoom -> chatRoom.getChatRoomTypes() == ChatRoomTypes.PRIVATE).toList();

        if (chatRoomList.isEmpty()) {
            return Optional.empty();
        } else {
            ChatRoom chatRoom = chatRoomList.get(0);
            return Optional.of(getChatRoomDto(chatRoom));
        }
    }
}
