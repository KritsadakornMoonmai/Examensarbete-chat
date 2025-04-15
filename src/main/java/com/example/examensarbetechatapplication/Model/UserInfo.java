package com.example.examensarbetechatapplication.Model;

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
    private ImageIcon profileImage;



    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private User user;
}
