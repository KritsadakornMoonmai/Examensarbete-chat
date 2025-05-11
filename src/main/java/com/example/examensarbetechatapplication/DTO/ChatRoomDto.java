package com.example.examensarbetechatapplication.DTO;

import com.example.examensarbetechatapplication.Model.ChatRoomTypes;
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
    private ChatRoomTypes chatRoomTypes;
    private List<ChatRoomMemberDtoMin> chatRoomMemberDtoMins;
    private List<MessageDtoMin> messageDtoMins;
}
