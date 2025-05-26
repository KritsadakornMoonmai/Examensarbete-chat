package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRoleRepository extends CrudRepository<UserRole, UUID> {
    UserRole findUserRoleByRole(String role);
    boolean existsUserRoleByRole(String role);
}
