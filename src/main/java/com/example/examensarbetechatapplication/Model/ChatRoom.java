package com.example.examensarbetechatapplication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.EAGER)
    private List<ChatRoomMember> chatRoomMembers;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.EAGER)
    private List<Message> messages;

    public ChatRoom(String name, LocalDateTime createAt) {
        this.name = name;
        this.createAt = createAt;
    }
}
