package com.example.examensarbetechatapplication.DTO;


import com.example.examensarbetechatapplication.Model.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDto {

    private long id;
    private String fullName;
    private int age;
    private String telephoneNumber;
    private ImageIcon profileImage;
    private UserDtoMin userDtoMin;
}
