package com.example.examensarbetechatapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDtoMin {
    private long id;
    private String name;
    private LocalDateTime createAt;
}
