package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.DTO.UserInfoDtoMin;
import com.example.examensarbetechatapplication.Model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService {


    private final UserService userService;

    protected UserInfoDto getUserInfoDto(UserInfo userInfo) {

        return UserInfoDto.builder()
                .id(userInfo.getId())
                .fullName(userInfo.getFullName())
                .age(userInfo.getAge())
                .telephoneNumber(userInfo.getTelephoneNumber())
                .profileImage(userInfo.getProfileImage())
                .userDtoMin(userService.getUserDtoMin(userInfo.getUser()))
                .build();
    }

    protected UserInfoDtoMin getUserInfoDtoMin(UserInfo userInfo) {
        return UserInfoDtoMin.builder()
                .id(userInfo.getId())
                .fullName(userInfo.getFullName())
                .telephoneNumber(userInfo.getTelephoneNumber())
                .build();
    }
}
