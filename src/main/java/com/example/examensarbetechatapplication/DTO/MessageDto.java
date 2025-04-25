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
public class MessageDto {

    private long id;
    private String contents;
    private LocalDateTime time;
    private ChatRoomMemberDtoMin chatRoomMemberDtoMin;
    private ChatRoomDtoMin chatRoomDtoMin;
}
