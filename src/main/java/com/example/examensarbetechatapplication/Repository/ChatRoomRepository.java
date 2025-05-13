package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.ChatRoomTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findChatRoomByChatRoomMembersAndChatRoomTypes(List<ChatRoomMember> list, ChatRoomTypes chatRoomTypes);

    @Query("""
    SELECT cr
    FROM ChatRoom cr
    JOIN cr.chatRoomMembers m
    WHERE m.user.id IN :userIds
    GROUP BY cr.id
    HAVING COUNT(DISTINCT m.user.id) = :size
       AND COUNT(m) = (
           SELECT COUNT(m2)
           FROM ChatRoomMember m2
           WHERE m2.chatRoom.id = cr.id
       )
""")
    List<ChatRoom> findChatRoomWithExactMembersByUserIds(
            @Param("userIds") List<UUID> userIds,
            @Param("size") long size
    );
}
