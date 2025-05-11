package com.example.examensarbetechatapplication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    @Id
    @GeneratedValue
    private long id;

    private String fullName;
    private int age;
    private String telephoneNumber;

    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public UserInfo(String fullName, int age, String telephoneNumber, byte[] profileImage, User user) {
        this.fullName = fullName;
        this.age = age;
        this.telephoneNumber = telephoneNumber;
        this.profileImage = profileImage;
        this.user = user;
    }
}
