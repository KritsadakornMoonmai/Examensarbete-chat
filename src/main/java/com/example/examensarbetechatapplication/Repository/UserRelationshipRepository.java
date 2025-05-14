package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Long> {

    boolean existsUserRelationshipByUser_UsernameAndFriend_Username(String userUsername, String friendUsername);

    UserRelationship findUserRelationshipByUserAndFriend(User user, User friend);
}