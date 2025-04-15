package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
