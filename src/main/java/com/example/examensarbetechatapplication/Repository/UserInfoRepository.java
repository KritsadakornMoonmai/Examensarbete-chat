package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    UserInfo getUserInfoByUser(User user);

    UserInfo getUserInfoById(long id);
}
