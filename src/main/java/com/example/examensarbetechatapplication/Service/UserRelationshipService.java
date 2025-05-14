package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserRelationshipDto;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Repository.UserRelationshipRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRelationshipService {

    private final UserRelationshipRepository userRelationshipRepo;

    private final UserRepository userRepo;

    protected UserRelationshipDto getUserRelationshipDtoFull(UserRelationship relationship){

        return UserRelationshipDto.builder()
                .id(relationship.getId())
                .relatedAt(relationship.getRelatedAt())
                .status(relationship.getStatus())
                .user(UserDtoMin.builder()
                        .id(relationship.getUser().getId())
                        .username(relationship.getUser().getUsername())
                        .email(relationship.getUser().getEmail())
                        .build())
                .friend(UserDtoMin.builder()
                        .id(relationship.getFriend().getId())
                        .username(relationship.getFriend().getUsername())
                        .email(relationship.getFriend().getEmail())
                        .build())
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

    public void saveUserRelationship(List<UserRelationshipDto> userRelationshipDtos) {
        List<UserRelationship> userRelationships = userRelationshipDtos
                .stream()
                .map(this::getUserRelationshipFromDto)
                .toList();

        userRelationshipRepo.saveAll(userRelationships);
    }

    public boolean findUserRelationShipIsExist(String senderUsername, String receiverUsername) {
        boolean isRelationExits = userRelationshipRepo.existsUserRelationshipByUser_UsernameAndFriend_Username(senderUsername, receiverUsername);
        boolean isRelationExitsReverse = userRelationshipRepo.existsUserRelationshipByUser_UsernameAndFriend_Username(receiverUsername, senderUsername);

        return isRelationExits || isRelationExitsReverse;
    }

    public UserRelationshipDto findRelationshipByUserAndFriend(String senderUsername, String receiverUsername) {
        User sender = userRepo.findByUsername(senderUsername);
        User receiver = userRepo.findByUsername(receiverUsername);

        UserRelationship userRelationship = userRelationshipRepo.findUserRelationshipByUserAndFriend(sender, receiver);

        return getUserRelationshipDtoFull(userRelationship);
    }

}
