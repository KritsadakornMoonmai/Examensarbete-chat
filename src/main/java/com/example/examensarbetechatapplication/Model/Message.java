package com.example.examensarbetechatapplication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue
    private long id;

    private String contents;

    private LocalDateTime time;

    @ManyToOne
    @JoinColumn
    private ChatRoomMember chatRoomMember;

    @ManyToOne
    @JoinColumn
    private ChatRoom chatRoom;

    public Message(String contents, LocalDateTime time, ChatRoomMember chatRoomMember, ChatRoom chatRoom) {
        this.contents = contents;
        this.time = time;
        this.chatRoomMember = chatRoomMember;
        this.chatRoom = chatRoom;
    }
}
