package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;
}
