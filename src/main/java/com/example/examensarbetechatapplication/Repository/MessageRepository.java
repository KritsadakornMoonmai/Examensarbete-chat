package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.DTO.ChatRoomDto;
import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDto;
import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    public Message getMessagesByChatRoomMember(ChatRoomMember chatRoomMember);

    public Message getMessageByChatRoomMemberAndChatRoom(ChatRoomMember chatRoomMember, ChatRoom chatRoom);
}
