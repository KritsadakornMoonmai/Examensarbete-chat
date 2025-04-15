package com.example.examensarbetechatapplication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRelationship {

    @Id
    @GeneratedValue
    private long id;

    private long user_id;
    private long receiver_id;
    private LocalDateTime relatedAt;

    @OneToMany(mappedBy = "userRelationship", fetch = FetchType.EAGER)
    private List<User> user;
}
