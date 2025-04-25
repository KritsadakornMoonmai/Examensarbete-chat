package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserInfo;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Repository.UserInfoRepository;
import com.example.examensarbetechatapplication.Repository.UserRelationshipRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserInfoRepository userInfoRepo;

    @Autowired
    UserRelationshipRepository userRelationshipRepo;

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

    protected User getUserFromUserDto(UserDto userDto) {
        UserInfo userInfo = userInfoRepo.findById(userDto.getUserInfoDtoMin().getId())
                .orElseThrow(() -> new RuntimeException("User Info not found"));

        List<UserRelationship> initiatedList = userDto.getRelationshipInitiatedDtos()
                .stream()
                .map(relationshipDto -> userRelationshipService.getUserRelationshipFromDto(relationshipDto))
                .toList();

        List<UserRelationship> receivedList = userDto.getRelationshipReceivedDtos()
                .stream()
                .map(relationshipDto -> userRelationshipService.getUserRelationshipFromDto(relationshipDto))
                .toList();

        List<ChatRoomMember> chatRoomMembers = userDto.getChatRoomMemberDtoMins()
                .stream()
                .map(memberDtoMin -> chatRoomMemberService.getChatRoomMemberFromDtoMini(memberDtoMin))
                .toList();

        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .userInfo(userInfo)
                .relationshipInitiated(initiatedList)
                .relationshipReceived(receivedList)
                .chatRoomMember(chatRoomMembers)
                .build();
    }

    public List<UserDto> getAllUser() {
        return userRepo.findAll().stream().map(this::getUserDto).toList();
    }

    public User getUserById(long id) {
        return userRepo.getReferenceById(id);
    }

    public User getUserFromUserDtoPub(UserDto userDto) {
        return getUserFromUserDto(userDto);
    }

    public UserDto getUserDtoById(long id) {
        return getUserDto(userRepo.getReferenceById(id));
    }

    public UserDtoMin getUserDtoMinById(long id) {
        return getUserDtoMin(userRepo.getReferenceById(id));
    }

    public UserDto getUserDtoByUsername(String username) {
        return getUserDto(userRepo.getUserByUsername(username));
    }

    public UserDtoMin getUserDtoMinByUsername(String username) {
        return getUserDtoMin(userRepo.getUserByUsername(username));
    }

    public List<UserDto> getUserByUserInfoFullName(String fullname) {
        List<UserInfoDto> userInfoDto = userInfoService.getUserInfoDtoByName(fullname);
        List<UserDto> userDtos = new ArrayList<>();

        for (UserInfoDto infoDto : userInfoDto) {
            for (int j = 0; j < getAllUser().size(); j++) {
                if (infoDto.getUserDtoMin().getId() == getAllUser().get(j).getId()) {
                    userDtos.add(getAllUser().get(j));
                }
            }
        }

        return userDtos;
    }

    public void createUser(UserDto userDto) {
        User newUser = getUserFromUserDto(userDto);
        userRepo.save(newUser);
    }

    public void deleteUser(UserDto userDto) {
        User user = getUserFromUserDto(userDto);
        userRepo.delete(user);
    }

}
