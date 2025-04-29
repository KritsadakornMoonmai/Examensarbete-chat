package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.DTO.UserInfoDtoMin;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserInfo;
import com.example.examensarbetechatapplication.Repository.UserInfoRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserInfoService {


    private final UserInfoRepository userInfoRepo;


    private final UserRepository userRepo;


    protected UserInfoDto getUserInfoDto(UserInfo userInfo) {

        return UserInfoDto.builder()
                .id(userInfo.getId())
                .fullName(userInfo.getFullName())
                .age(userInfo.getAge())
                .telephoneNumber(userInfo.getTelephoneNumber())
                .profileImage(userInfo.getProfileImage())
                .userDtoMin(UserDtoMin.builder()
                        .id(userInfo.getUser().getId())
                        .username(userInfo.getUser().getUsername())
                        .email(userInfo.getUser().getEmail())
                        .build())
                .build();
    }

    protected UserInfoDtoMin getUserInfoDtoMin(UserInfo userInfo) {
        return UserInfoDtoMin.builder()
                .id(userInfo.getId())
                .fullName(userInfo.getFullName())
                .telephoneNumber(userInfo.getTelephoneNumber())
                .build();
    }

    protected UserInfo getUserInfoFromDto(UserInfoDto userInfoDto) {

        User user = userRepo.findById(userInfoDto.getUserDtoMin().getId())
                .orElseThrow(() ->new RuntimeException("User not found"));

        return UserInfo.builder()
                .id(userInfoDto.getId())
                .fullName(userInfoDto.getFullName())
                .age(userInfoDto.getAge())
                .telephoneNumber(userInfoDto.getTelephoneNumber())
                .profileImage(userInfoDto.getProfileImage())
                .user(user).build();
    }


    public UserInfoDto getUserInfoDtoById(long id) {
        return getUserInfoDto(userInfoRepo.findById(id).orElseThrow(() -> new RuntimeException("User info not exist")));
    }

    public UserInfoDtoMin getUserInfoDtoMinById(long id) {
        return getUserInfoDtoMin(userInfoRepo.findById(id).orElseThrow(() -> new RuntimeException("User info not exist")));
    }

    public UserInfoDto getUserInfoByUserDto(UserDto userDto) {
        return getUserInfoDto(userInfoRepo.getUserInfoByUser(userRepo.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User info not found"))));
    }

    public List<UserInfoDto> getUserInfoDtoByName(String name) {
        List<UserInfo> userInfoList = userInfoRepo.findAll().stream().filter(userInfo -> Objects.equals(userInfo.getFullName(), name)).toList();

        return userInfoList.stream().map(this::getUserInfoDto).toList();
    }

    public void createUserInfo(UserInfoDto userInfoDto) {
        UserInfo userInfo = getUserInfoFromDto(userInfoDto);

        userInfoRepo.save(userInfo);
    }

}
