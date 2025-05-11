package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserInfoDto;
import com.example.examensarbetechatapplication.DTO.UserInfoDtoMin;
import com.example.examensarbetechatapplication.Model.RelationshipStatus;
import com.example.examensarbetechatapplication.Model.User;
import com.example.examensarbetechatapplication.Model.UserInfo;
import com.example.examensarbetechatapplication.Model.UserRelationship;
import com.example.examensarbetechatapplication.Repository.UserInfoRepository;
import com.example.examensarbetechatapplication.Repository.UserRelationshipRepository;
import com.example.examensarbetechatapplication.Repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

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
    void setUp() {
        newUser1.setId(id1);
        newUser2.setId(id2);
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
                , userInfoService
                , userRelationshipService
                , chatRoomMemberService);

        /*when(userRepository.findById(1L)).thenReturn(Optional.of(newUser1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser2));
        when(userRepository.findByUsername("myTestUsername")).thenReturn(newUser1);
        when(userRepository.findByUsername("myTestUsername2")).thenReturn(newUser2);
        when(userRepository.saveAll(anyList())).thenReturn(userLists);
        when(userRepository.findAll()).thenReturn(userLists);

        when(userInfoRepository.findById(1L)).thenReturn(Optional.of(newUserInfo));
        when(userInfoRepository.findById(2L)).thenReturn(Optional.of(newUserInfo2));
        when(userInfoRepository.saveAll(anyList())).thenReturn(userInfoList);
        when(userInfoRepository.findAll()).thenReturn(userInfoList);

        when(userRelationshipRepository.saveAll(anyList())).thenReturn(userRelationshipLists);*/
    }

    @Test
    void getUserInfoDto() {
        UserInfoDto getInfoDto = userInfoService.getUserInfoDto(newUserInfo);

        assertThat(getInfoDto).isNotNull();
        assertEquals(getInfoDto.getFullName(), "JD");
        assertEquals(getInfoDto.getUserDtoMin().getUsername(), "myTestUsername");
        assertNotEquals(getInfoDto.getAge(), 31);
    }

    @Test
    void getUserInfoDtoMin() {
        UserInfoDtoMin getInfoDtoMin = userInfoService.getUserInfoDtoMin(newUserInfo2);

        assertThat(getInfoDtoMin).isNotNull();
        assertEquals(getInfoDtoMin.getId(), 2L);
        assertEquals(getInfoDtoMin.getFullName(), "AP");
        assertNotEquals(getInfoDtoMin.getTelephoneNumber(), "123456");
    }

    @Test
    void getUserInfoFromDto() {

        when(userInfoRepository.findById(1L)).thenReturn(Optional.of(newUserInfo));
        UserInfoDto getUserInfoDto = userInfoService.getUserInfoDtoById(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser1));
        UserInfo getUserInfo = userInfoService.getUserInfoFromDto(getUserInfoDto);
        verify(userRepository).findById(1L);

        assertThat(getUserInfo).isNotNull();
        assertEquals(getUserInfo.getFullName(), "JD");
        assertEquals(getUserInfo.getUser().getUsername(), "myTestUsername");
        assertNotEquals(getUserInfo.getAge(), 31);
    }

    @Test
    void getUserInfoDtoById() {
        when(userInfoRepository.findById(1L)).thenReturn(Optional.of(newUserInfo));
        UserInfoDto getInfoDto = userInfoService.getUserInfoDtoById(1L);
        verify(userInfoRepository).findById(1L);

        assertThat(getInfoDto).isNotNull();
        assertEquals(getInfoDto.getFullName(), "JD");
        assertEquals(getInfoDto.getUserDtoMin().getUsername(), "myTestUsername");
        assertNotEquals(getInfoDto.getAge(), 31);
    }

    @Test
    void getUserInfoDtoMinById() {
        when(userInfoRepository.findById(2L)).thenReturn(Optional.of(newUserInfo2));
        UserInfoDtoMin getInfoDtoMin = userInfoService.getUserInfoDtoMinById(2L);

        assertThat(getInfoDtoMin).isNotNull();
        assertEquals(getInfoDtoMin.getId(), 2L);
        assertEquals(getInfoDtoMin.getFullName(), "AP");
        assertNotEquals(getInfoDtoMin.getTelephoneNumber(), "123456");
    }

    @Test
    void getUserInfoByUserDto() {
        UserDto getUserDto = userService.getUserDto(newUser1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser1));
        when(userInfoRepository.getUserInfoByUser(newUser1)).thenReturn(newUserInfo);
        UserInfoDto getInfoDto = userInfoService.getUserInfoByUserDto(getUserDto);

        verify(userRepository).findById(1L);
        verify(userInfoRepository).getUserInfoByUser(newUser1);

        assertThat(getInfoDto).isNotNull();
        assertEquals(getInfoDto.getFullName(), "JD");
        assertEquals(getInfoDto.getUserDtoMin().getUsername(), "myTestUsername");
        assertNotEquals(getInfoDto.getAge(), 31);

    }

    @Test
    void getUserInfoDtoByName() {
        when(userInfoRepository.findAll()).thenReturn(userInfoList);
        List<UserInfoDto> getUserInfoDto = userInfoService.getUserInfoDtoByName("AP");

        verify(userInfoRepository).findAll();

        assertThat(getUserInfoDto.get(0)).isNotNull();
        assertEquals(getUserInfoDto.get(0).getId(), 2L);
        assertEquals(getUserInfoDto.get(0).getFullName(), "AP");
        assertNotEquals(getUserInfoDto.get(0).getTelephoneNumber(), "123456");
    }

    @Test
    void createUserInfo() {

        User newUser3 = new User("myTestUsername345", "myPassword99", "thirdEmail@something.com");
        UserInfo newUser3Info = new UserInfo("TD", 15, "98764", null, newUser3);
        newUser3Info.setId(3L);
        newUser3.setId(3L);
        newUser3.setUserInfo(newUser3Info);
        UserInfoDto user3InfoDto = userInfoService.getUserInfoDto(newUser3Info);

        when(userRepository.findById(3L)).thenReturn(Optional.of(newUser3));
        when(userInfoRepository.findById(3L)).thenReturn(Optional.of(newUser3Info));
        userInfoService.createUserInfo(user3InfoDto);

        verify(userRepository).findById(3L);
        verify(userInfoRepository).save(newUser3Info);

        UserInfo getUserInfoById = userInfoRepository.findById(3L).orElseThrow(() -> new RuntimeException("User info Not found"));

        verify(userInfoRepository).findById(3L);
        assertNotNull(getUserInfoById);
        assertEquals(getUserInfoById.getFullName(), "TD");
        assertEquals(getUserInfoById.getAge(), 15);
        assertEquals(getUserInfoById.getUser(), newUser3);
        assertNotEquals(getUserInfoById.getTelephoneNumber(), "1234");
    }
}