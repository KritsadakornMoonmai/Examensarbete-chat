package com.example.examensarbetechatapplication.DTO;

import com.example.examensarbetechatapplication.Model.ChatRoom;
import com.example.examensarbetechatapplication.Model.Message;
import com.example.examensarbetechatapplication.Model.Roles;
import com.example.examensarbetechatapplication.Model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomMemberDto {
    private long id;
    private LocalDateTime joinedAt;
    private UserDtoMin userDtoMin;
    private ChatRoomDtoMin chatRoomDtoMin;
    private List<MessageDtoMin> messageDtoMins;
    private Roles roles;
}
