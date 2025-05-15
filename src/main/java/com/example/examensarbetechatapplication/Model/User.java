package com.example.examensarbetechatapplication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;
    private String password;
    private String email;
    private boolean enable;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserInfo userInfo;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserRelationship> relationshipInitiated = new ArrayList<>();

    @OneToMany(mappedBy = "friend", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserRelationship> relationshipReceived = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ChatRoomMember> chatRoomMember = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_role_id")
    )
    private Collection<UserRole> roles;


    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
