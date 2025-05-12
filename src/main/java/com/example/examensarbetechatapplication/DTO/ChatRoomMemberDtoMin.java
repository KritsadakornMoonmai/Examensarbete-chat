package com.example.examensarbetechatapplication.DTO;

import com.example.examensarbetechatapplication.Model.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomMemberDtoMin {

    private long id;
    private LocalDateTime joinedAt;
    private String memberName;
    private Roles roles;
}
