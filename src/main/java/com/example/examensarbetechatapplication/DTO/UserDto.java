package com.example.examensarbetechatapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private long id;

    private String username;
    private String password;
    private String email;
    private UserInfoDtoMin userInfoDtoMin;
    private List<UserRelationshipDto> relationshipInitiatedDtos;
    private List<UserRelationshipDto> relationshipReceivedDtos;
    private List<ChatRoomMemberDtoMin> chatRoomMemberDtoMins;
}
