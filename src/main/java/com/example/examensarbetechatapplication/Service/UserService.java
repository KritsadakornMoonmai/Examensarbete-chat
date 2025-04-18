package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserRelationshipDto;
import com.example.examensarbetechatapplication.DTO.UserRelationshipDtoMin;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserRelationshipService userRelationshipService;

    @Autowired
    ChatRoomMemberService chatRoomMemberService;

    protected UserDto getUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .userInfoDtoMin(userInfoService.getUserInfoDtoMin(user.getUserInfo()))
                .relationshipInitiatedDtos(
                        user.getRelationshipInitiated()
                                .stream()
                                .map(userRelationship -> userRelationshipService.getUserRelationshipDtoFull(userRelationship))
                                .toList()
                )
                .relationshipReceivedDtos(
                        user.getRelationshipReceived()
                                .stream()
                                .map(userRelationship -> userRelationshipService.getUserRelationshipDtoFull(userRelationship))
                                .toList()
                )
                .chatRoomMemberDtoMins(
                        user.getChatRoomMember()
                                .stream()
                                .map(member -> chatRoomMemberService.getChatMemberDtoMin(member))
                                .toList()
                )
                .build();
    }

    protected UserDtoMin getUserDtoMin(User user) {

        return UserDtoMin.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public List<UserDto> getAllUser() {
        return userRepo.findAll().stream().map(this::getUserDto).toList();
    };

    public UserDto getUserById(long id) {
        return getUserDto(userRepo.getReferenceById(id));
    }

}
