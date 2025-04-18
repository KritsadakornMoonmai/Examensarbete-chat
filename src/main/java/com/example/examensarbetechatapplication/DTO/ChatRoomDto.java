package com.example.examensarbetechatapplication.DTO;

import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.Message;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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
public class ChatRoomDto {
    private long id;
    private String name;
    private LocalDateTime createAt;
    private List<ChatRoomMemberDtoMin> chatRoomMemberDtoMins;
    private List<MessageDtoMin> messageDtoMins;
}
