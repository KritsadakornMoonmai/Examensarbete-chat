package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.DTO.UserInfoDtoMin;
import com.example.examensarbetechatapplication.Model.*;
import com.example.examensarbetechatapplication.Repository.UserInfoRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import com.example.examensarbetechatapplication.Repository.UserRoleRepository;
import com.example.examensarbetechatapplication.security.ConcreteUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {


    final private UserRepository userRepo;
    final private UserInfoRepository userInfoRepo;
    final private UserRoleRepository userRoleRepo;

    final private UserInfoService userInfoService;

    final private UserRelationshipService userRelationshipService;

    final private ChatRoomMemberService chatRoomMemberService;

    protected UserDto getUserDto(User user) {

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
                .enable(user.isEnable())
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

    public boolean findIfUserOrEmailExist(String username, String email) {
        return userRepo.existsByUsernameOrEmail(username, email);
    }

    protected User getUserFromUserDto(UserDto userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .enable(userDto.isEnable())
                .build();

        Optional<UserInfo> userInfo = userInfoRepo.findById(userDto.getUserInfoDtoMin().getId());
        UserInfo finalUserInfo = userInfo.orElseGet(UserInfo::new);

        user.setUserInfo(finalUserInfo);
        finalUserInfo.setUser(user);

        /*List<UserRelationship> initiatedList = userDto.getRelationshipInitiatedDtos()
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
                .toList();*/

        return user;
    }

    public List<UserDto> getAllUser() {
        List<UserDto> userDtos = new ArrayList<>();
        userRepo.findAll().forEach(user -> userDtos.add(getUserDto(user)));

        return userDtos;
    }

    public UserDto getUserDtoById(UUID id) {
        User user = userRepo.findById(id).orElseThrow(() ->new RuntimeException("User not found"));
        return getUserDto(user);
    }

    public UserDtoMin getUserDtoMinById(UUID id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getUserDtoMin(user);
    }

    public UserDto getUserDtoByUsername(String username) {
        return getUserDto(userRepo.findByUsername(username));
    }

    public List<UserDto> getUserDtosByUsername(String username) {
        List<User> userList = userRepo.findUsersByUsernameIsContaining(username);

        return userList.stream().map(this::getUserDto).toList();
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

    public UserRole getUserRole(String role) {
        return userRoleRepo.findUserRoleByRole(role);
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
