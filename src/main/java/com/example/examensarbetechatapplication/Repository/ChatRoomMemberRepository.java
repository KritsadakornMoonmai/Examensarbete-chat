package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    public ChatRoomMember findChatRoomMemberByUserId(UUID id);
}
