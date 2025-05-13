package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserRelationshipDto;
import com.example.examensarbetechatapplication.Model.RelationshipStatus;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserInfo;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Repository.UserInfoRepository;
import com.example.examensarbetechatapplication.Repository.UserRelationshipRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
import com.example.examensarbetechatapplication.Repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
class UserRelationshipServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRelationshipRepository userRelationshipRepository;


    @Autowired
    @InjectMocks
    private UserInfoService userInfoService;

    @Autowired
    @InjectMocks
    private UserService userService;

    @Autowired
    @InjectMocks
    private UserRelationshipService userRelationshipService;

    @Autowired
    @InjectMocks
    private ChatRoomMemberService chatRoomMemberService;

    long id1 = 1L;
    long id2 = 2L;

    User newUser1 = new User("myTestUsername", "myPassword123", "myEmail@blabla.com");
    User newUser2 = new User("myTestUsername2", "myPassword12345", "myEmail2@blabla.com");
    UserInfo newUserInfo = new UserInfo("JD", 30, "123456", null, newUser1);
    UserInfo newUserInfo2 = new UserInfo("AP", 22, "52151", null, newUser2);

    LocalDateTime localDateTime = LocalDateTime.of(2025, 2, 11, 12, 11, 25);

    RelationshipStatus pending = RelationshipStatus.ACCEPTED;

    UserRelationship relationship1 = new UserRelationship(1L, localDateTime, pending, newUser1, newUser2);
    UserRelationship relationship2 = new UserRelationship(2L, localDateTime, pending, newUser2, newUser1);

    List<UserRelationship> user1Initiated = List.of(relationship1);
    List<UserRelationship> user2Received = List.of(relationship2);

    List<User> userLists = List.of(newUser1, newUser2);

    List<UserInfo> userInfoList = List.of(newUserInfo, newUserInfo2);

    List<UserRelationship> userRelationshipLists = List.of(relationship1, relationship2);

    @BeforeEach
    void setUps() {
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        newUser1.setId(uuid);
        newUser2.setId(uuid2);
        newUserInfo.setId(id1);
        newUserInfo2.setId(id2);
        newUser1.setUserInfo(newUserInfo);
        newUser2.setUserInfo(newUserInfo2);

        newUser1.setRelationshipInitiated(user1Initiated);
        newUser2.setRelationshipReceived(user2Received);

        userInfoService = new UserInfoService(
                userInfoRepository
                , userRepository
        );

        userRelationshipService = new UserRelationshipService(userRepository);

        userService = new UserService(userRepository
                , userInfoRepository
                , userRoleRepository
                , userInfoService
                , userRelationshipService
                , chatRoomMemberService);

        /*when(userRepository.findById(1L)).thenReturn(Optional.of(newUser1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser2));
        when(userRepository.findByUsername("myTestUsername")).thenReturn(newUser1);
        when(userRepository.findByUsername("myTestUsername2")).thenReturn(newUser2);
        when(userRepository.saveAll(anyList())).thenReturn(userLists);
        when(userRepository.findAll()).thenReturn(userLists);


        when(userInfoRepository.saveAll(anyList())).thenReturn(userInfoList);
        when(userRelationshipRepository.saveAll(anyList())).thenReturn(userRelationshipLists);*/

    }

    @Test
    void getUserRelationshipDtoFull() {
        UserRelationshipDto URD = userRelationshipService.getUserRelationshipDtoFull(relationship1);

        assertEquals(URD.getUser().getUsername(), "myTestUsername");
        assertEquals(URD.getFriend().getUsername(), "myTestUsername2");
        assertNotEquals(URD.getUser().getEmail(), "myEmail2@blabla.com");
    }

    /*@Test
    void getUserRelationshipFromDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser2));
        UserRelationshipDto URD = userRelationshipService.getUserRelationshipDtoFull(relationship2);
        UserRelationship getUR = userRelationshipService.getUserRelationshipFromDto(URD);

        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);

        assertEquals(getUR.getUser().getUsername(), "myTestUsername2");
        assertEquals(getUR.getFriend().getUsername(), "myTestUsername");
        assertNotEquals(getUR.getUser().getEmail(), "myEmail@blabla.com");
    }*/
}