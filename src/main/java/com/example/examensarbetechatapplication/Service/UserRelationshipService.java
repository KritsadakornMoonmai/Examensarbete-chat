package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserRelationshipDto;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRelationshipService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    protected UserRelationshipDto getUserRelationshipDtoFull(UserRelationship relationship){

        return UserRelationshipDto.builder()
                .id(relationship.getId())
                .relatedAt(relationship.getRelatedAt())
                .status(relationship.getStatus())
                .user(userService.getUserDtoMin(relationship.getUser()))
                .friend(userService.getUserDtoMin(relationship.getFriend()))
                .build();
    }

    protected UserRelationship getUserRelationshipFromDto(UserRelationshipDto dto) {
        User user = userRepo.findById(dto.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User friend = userRepo.findById(dto.getFriend().getId())
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        return UserRelationship.builder()
                .id(dto.getId())
                .relatedAt(dto.getRelatedAt())
                .status(dto.getStatus())
                .user(user)
                .friend(friend)
                .build();
    }

}
