package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}
