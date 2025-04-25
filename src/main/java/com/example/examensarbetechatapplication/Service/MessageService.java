package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.ChatRoomDtoMin;
import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDtoMin;
import com.example.examensarbetechatapplication.DTO.MessageDto;
import com.example.examensarbetechatapplication.DTO.MessageDtoMin;
import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.Message;
import com.example.examensarbetechatapplication.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    @Autowired
    MessageRepository messageRepo;

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
                .chatRoomDtoMin(chatRoomService.getChatRoomDtoMin(message.getChatRoom()))
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
                .chatRoomMember(chatRoomMemberService.getChatRoomMemberById(messageDto.getChatRoomMemberDtoMin().getId()))
                .chatRoom(chatRoomService.getChatRoomById(messageDto.getChatRoomDtoMin().getId()))
                .build();
    }

    public MessageDto getMessageDtoByOwner(ChatRoomMember chatRoomMember) {
        return getMessageDto(messageRepo.getMessagesByChatRoomMember(chatRoomMember));
    }

    public MessageDto getMessageHandler(ChatRoomMemberDtoMin CRMDMini, ChatRoomDtoMin CRDMini) {
        ChatRoomMember chatRoomMember = chatRoomMemberService.getChatRoomMemberById(CRMDMini.getId());
        ChatRoom chatRoom = chatRoomService.getChatRoomById(CRDMini.getId());

        return getMessageByOwnerAndChatRoom(chatRoomMember, chatRoom);
    }

    public MessageDto getMessageByOwnerAndChatRoom(ChatRoomMember chatRoomMember, ChatRoom chatRoom) {
        Message getMessage = messageRepo.getMessageByChatRoomMemberAndChatRoom(chatRoomMember, chatRoom);
        return getMessageDto(getMessage);
    }

    public void saveMessage(MessageDto messageDto) {
        Message message = getMessageFromDto(messageDto);
        messageRepo.save(message);
    }
}
