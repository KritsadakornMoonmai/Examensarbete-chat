package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.DTO.UserInfoDtoMin;
import com.example.examensarbetechatapplication.Model.ChatRoomMember;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserInfo;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Repository.UserInfoRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import com.example.examensarbetechatapplication.security.ConcreteUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {


    final private UserRepository userRepo;
    final private UserInfoRepository userInfoRepo;

    final private UserInfoService userInfoService;

    final private UserRelationshipService userRelationshipService;

    final private ChatRoomMemberService chatRoomMemberService;

    protected UserDto getUserDto(User user) {

        System.out.println("UserInfoID: "+user.getUserInfo().getId());
        UserInfoDtoMin userInfoDtoMin = new UserInfoDtoMin(
                user.getUserInfo().getId(),
                user.getUserInfo().getFullName(),
                user.getUserInfo().getTelephoneNumber()
        );

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .userInfoDtoMin(userInfoDtoMin)
                .relationshipInitiatedDtos(
                        user.getRelationshipInitiated()
                                .stream()
                                .map(userRelationshipService::getUserRelationshipDtoFull)
                                .toList()
                )
                .relationshipReceivedDtos(
                        user.getRelationshipReceived()
                                .stream()
                                .map(userRelationshipService::getUserRelationshipDtoFull)
                                .toList()
                )
                .chatRoomMemberDtoMins(
                        user.getChatRoomMember()
                                .stream()
                                .map(chatRoomMemberService::getChatMemberDtoMin)
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
        System.out.println("UserDto UserInfo Id: "+userDto.getUserInfoDtoMin().getId());
        UserInfo userInfo = userInfoRepo.findById(userDto.getUserInfoDtoMin().getId())
                .orElseThrow(() -> new RuntimeException("User Info not found"));

        List<UserRelationship> initiatedList = userDto.getRelationshipInitiatedDtos()
                .stream()
                .map(userRelationshipService::getUserRelationshipFromDto)
                .toList();

        List<UserRelationship> receivedList = userDto.getRelationshipReceivedDtos()
                .stream()
                .map(userRelationshipService::getUserRelationshipFromDto)
                .toList();

        List<ChatRoomMember> chatRoomMembers = userDto.getChatRoomMemberDtoMins()
                .stream()
                .map(chatRoomMemberService::getChatRoomMemberFromDtoMini)
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

    public UserDto getUserDtoById(long id) {
        User user = userRepo.findById(id).orElseThrow(() ->new RuntimeException("User not found"));
        return getUserDto(user);
    }

    public UserDtoMin getUserDtoMinById(long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getUserDtoMin(user);
    }

    public UserDto getUserDtoByUsername(String username) {
        return getUserDto(userRepo.findByUsername(username));
    }

    public UserDtoMin getUserDtoMinByUsername(String username) {
        return getUserDtoMin(userRepo.findByUsername(username));
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

    public UserDto fetchUser(String username, String password) {

        UserDto userDto = getUserDtoByUsername(username);
        if (userDto == null) {
            return null;
        } else if (Objects.equals(userDto.getPassword(), password)) {
            return userDto;
        } else {
            return null;
        }
    }

    public void createUser(UserDto userDto) {
        User newUser = getUserFromUserDto(userDto);
        userRepo.save(newUser);
    }

    public void deleteUser(UserDto userDto) {
        User user = getUserFromUserDto(userDto);
        userRepo.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        return new ConcreteUserDetail(user);
    }
}
