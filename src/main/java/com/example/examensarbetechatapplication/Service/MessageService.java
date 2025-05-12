package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.ChatRoomDtoMin;
import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDtoMin;
import com.example.examensarbetechatapplication.DTO.MessageDto;
import com.example.examensarbetechatapplication.DTO.MessageDtoMin;
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
public class MessageService {


    private final MessageRepository messageRepo;

    private final ChatRoomRepository chatRoomRepo;

    private final ChatRoomMemberRepository chatRoomMemberRepo;


    protected MessageDto getMessageDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .contents(message.getContents())
                .time(message.getTime())
                .chatRoomMemberDtoMin(ChatRoomMemberDtoMin.builder()
                        .id(message.getChatRoomMember().getId())
                        .joinedAt(message.getChatRoomMember().getJoinedAt())
                        .memberName(message.getChatRoomMember().getUser().getUsername())
                        .roles(message.getChatRoomMember().getRoles())
                        .build())
                .chatRoomDtoMin(ChatRoomDtoMin.builder()
                        .id(message.getChatRoom().getId())
                        .name(message.getChatRoom().getName())
                        .createAt(message.getChatRoom().getCreateAt())
                        .build())
                .build();
    }

    protected MessageDtoMin getMessageDtoMin(Message message) {
        return MessageDtoMin.builder()
                .id(message.getId())
                .contents(message.getContents())
                .time(message.getTime())
                .build();
    }

    protected Message getMessageFromDto(MessageDto messageDto) {
        return Message.builder()
                .id(messageDto.getId())
                .contents(messageDto.getContents())
                .time(messageDto.getTime())
                .chatRoomMember(chatRoomMemberRepo.getReferenceById(messageDto.getChatRoomMemberDtoMin().getId()))
                .chatRoom(chatRoomRepo.getReferenceById(messageDto.getChatRoomDtoMin().getId()))
                .build();
    }

    public MessageDto getMessageDtoByOwner(ChatRoomMember chatRoomMember) {
        return getMessageDto(messageRepo.getMessagesByChatRoomMember(chatRoomMember));
    }

    public MessageDto getMessageHandler(ChatRoomMemberDtoMin CRMDMini, ChatRoomDtoMin CRDMini) {
        ChatRoomMember chatRoomMember = chatRoomMemberRepo.getReferenceById(CRMDMini.getId());
        ChatRoom chatRoom = chatRoomRepo.getReferenceById(CRDMini.getId());

        return getMessageByOwnerAndChatRoom(chatRoomMember, chatRoom);
    }

    public MessageDto getMessageByOwnerAndChatRoom(ChatRoomMember chatRoomMember, ChatRoom chatRoom) {
        Message getMessage = messageRepo.getMessageByChatRoomMemberAndChatRoom(chatRoomMember, chatRoom);
        return getMessageDto(getMessage);
    }

    public Message getMessageById(long id) {
        return messageRepo.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public List<MessageDto> getAllMessage() {

        return messageRepo.findAll().stream()
                .map(this::getMessageDto)
                .toList();
    }

    public void saveMessage(MessageDto messageDto) {
        Message message = getMessageFromDto(messageDto);
        messageRepo.save(message);
    }
}
