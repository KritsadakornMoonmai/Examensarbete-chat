package com.example.examensarbetechatapplication.DTO;


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
    private byte[] profileImage;
    private UserDtoMin userDtoMin;
}
