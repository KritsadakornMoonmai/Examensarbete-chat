package com.example.examensarbetechatapplication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class ChatRoomMember {

    @Id
    @GeneratedValue
    private long id;

    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "chatRoomMember", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Message> messages;

    @Enumerated
    private Roles roles;

    public ChatRoomMember(User user, LocalDateTime joinedAt, ChatRoom chatRoom, Roles roles) {
        this.user = user;
        this.joinedAt = joinedAt;
        this.chatRoom = chatRoom;
        this.roles = roles;
    }
}
