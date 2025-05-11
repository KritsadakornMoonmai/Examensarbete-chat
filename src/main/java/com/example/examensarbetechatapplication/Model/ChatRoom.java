package com.example.examensarbetechatapplication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private ChatRoomTypes chatRoomTypes;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChatRoomMember> chatRoomMembers;

    @OneToMany(mappedBy = "chatRoom")
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    public ChatRoom(String name, LocalDateTime createAt, ChatRoomTypes chatRoomTypes) {
        this.name = name;
        this.createAt = createAt;
        this.chatRoomTypes = chatRoomTypes;
    }
}
