package com.example.examensarbetechatapplication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;
    private String email;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserInfo userInfo;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserRelationship> relationshipInitiated;

    @OneToMany(mappedBy = "friend")
    @JsonIgnore
    private List<UserRelationship> relationshipReceived;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ChatRoomMember> chatRoomMember;


    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
