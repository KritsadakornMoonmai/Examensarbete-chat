package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserRelationshipDto;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRelationshipService {

    @Autowired
    private UserService userService;

    protected UserRelationshipDto getUserRelationshipDtoFull(UserRelationship relationship){

        return UserRelationshipDto.builder()
                .id(relationship.getId())
                .relatedAt(relationship.getRelatedAt())
                .status(relationship.getStatus())
                .user(userService.getUserDtoMin(relationship.getUser()))
                .friend(userService.getUserDtoMin(relationship.getFriend()))
                .build();
    }
}
