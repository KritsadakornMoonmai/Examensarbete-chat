package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    User findByUsername(String username);
    boolean existsByUsernameOrEmail(String username, String email);

    User getReferenceById(UUID id);
}
