package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> getUserByUsername(String username);
}
