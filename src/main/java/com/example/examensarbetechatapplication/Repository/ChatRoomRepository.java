package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.ChatRoomTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findChatRoomByChatRoomMembersAndChatRoomTypes(List<ChatRoomMember> list, ChatRoomTypes chatRoomTypes);

    @Query("""
    SELECT cr
    FROM ChatRoom cr
    JOIN cr.chatRoomMembers m
    WHERE m.user.id IN :memberIds
    GROUP BY cr.id
    HAVING COUNT(DISTINCT m.user.id) = :size
""")
    List<ChatRoom> findChatRoomWithExactMembers(
            @Param("memberIds") List<Long> memberIds,
            @Param("size") long size
    );
}
