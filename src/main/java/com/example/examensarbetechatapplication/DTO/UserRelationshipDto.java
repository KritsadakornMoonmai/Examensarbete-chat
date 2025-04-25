package com.example.examensarbetechatapplication.DTO;

import com.example.examensarbetechatapplication.Model.RelationshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRelationshipDto {
    private long id;
    private LocalDateTime relatedAt;
    private RelationshipStatus status;
    private UserDtoMin user;
    private UserDtoMin friend;
}
