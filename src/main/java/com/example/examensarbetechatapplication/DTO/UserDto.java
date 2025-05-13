package com.example.examensarbetechatapplication.DTO;

import com.example.examensarbetechatapplication.Model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private UUID id;

    private String username;
    private String password;
    private String email;
    private boolean enable;
    private Collection<UserRole> userRole;
    private UserInfoDtoMin userInfoDtoMin;
    private List<UserRelationshipDto> relationshipInitiatedDtos;
    private List<UserRelationshipDto> relationshipReceivedDtos;
    private List<ChatRoomMemberDtoMin> chatRoomMemberDtoMins;
}
