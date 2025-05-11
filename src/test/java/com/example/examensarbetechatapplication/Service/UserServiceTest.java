package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.UserDto;
import com.example.examensarbetechatapplication.DTO.UserDtoMin;
import com.example.examensarbetechatapplication.DTO.UserRelationshipDto;
import com.example.examensarbetechatapplication.Model.*;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser2));
        when(userRepository.findByUsername("myTestUsername")).thenReturn(newUser1);
        when(userRepository.findByUsername("myTestUsername2")).thenReturn(newUser2);
        when(userRepository.saveAll(anyList())).thenReturn(userLists);
        when(userRepository.findAll()).thenReturn(userLists);


        when(userInfoRepository.saveAll(anyList())).thenReturn(userInfoList);
        when(userRelationshipRepository.saveAll(anyList())).thenReturn(userRelationshipLists);
    }

    @Test
    void getUserDto() {
        User getUser = userRepository.findByUsername("myTestUsername");
        User getUser2 = userRepository.findByUsername("myTestUsername2");
        verify(userRepository).findByUsername("myTestUsername");
        verify(userRepository).findByUsername("myTestUsername2");
        assertThat(getUser).isNotNull();
        UserDto newUserDto = userService.getUserDto(getUser);
        UserDtoMin newUser2DtoMin = userService.getUserDtoMin(getUser2);

        assertThat(getUser).isNotNull();
        assertEquals(getUser.getEmail(), newUserDto.getEmail());
        assertEquals(newUserDto.getEmail(), "myEmail@blabla.com");
        assertEquals(newUserDto.getId(), 1);
        assertNotEquals(newUserDto.getUsername(), "myTestUsername2");
        assertNotEquals(newUserDto.getEmail(), "myPassword12345");

        assertNotNull(newUserDto.getRelationshipInitiatedDtos());
        assertEquals(newUserDto.getRelationshipInitiatedDtos().get(0).getUser().getUsername(), newUserDto.getUsername());
        assertEquals(newUserDto.getRelationshipInitiatedDtos().get(0).getFriend().getUsername(), newUser2DtoMin.getUsername());
        assertTrue(newUserDto.getRelationshipInitiatedDtos().get(0).getStatus() == RelationshipStatus.ACCEPTED);
    }

    @Test
    void getUserDtoMin() {
        UserDtoMin newUser2DtoMin = userService.getUserDtoMin(newUser2);

        assertThat(newUser2DtoMin).isNotNull();
        assertEquals(newUser2DtoMin.getUsername(), newUser2.getUsername());
        assertEquals(newUser2DtoMin.getEmail(), newUser2.getEmail());
        assertNotEquals(newUser2DtoMin.getEmail(), "myEmail@blabla.com");
    }


    @Test
    void getUserFromUserDto() {
        UserRelationshipDto userRelationshipDto = userRelationshipService.getUserRelationshipDtoFull(user1Initiated.get(0));

        User user = userRepository.findByUsername("myTestUsername");
        assertThat(user).isNotNull();
        UserDto user1Dto = userService.getUserDto(user);
        assertThat(user1Dto).isNotNull();
        assertEquals(user1Dto.getUserInfoDtoMin().getId(), 1L);

        when(userInfoRepository.findById(user1Dto.getUserInfoDtoMin().getId()))
                .thenReturn(Optional.of(newUserInfo));

        User getUserFromDto = userService.getUserFromUserDto(user1Dto);

        verify(userInfoRepository).findById(user1Dto.getUserInfoDtoMin().getId());
        verify(userRepository).findById(userRelationshipDto.getUser().getId());
        verify(userRepository).findById(userRelationshipDto.getFriend().getId());

        assertThat(getUserFromDto).isNotNull();
        assertEquals(getUserFromDto.getUsername(), "myTestUsername");
        assertNotEquals(getUserFromDto.getUsername(), "myTestUsername2");
    }


    @Test
    void getAllUser() {
        List<UserDto> allUser = userService.getAllUser();

        verify(userRepository).findAll();
        assertEquals(allUser.get(0).getUsername(), "myTestUsername");
        assertEquals(allUser.get(1).getPassword(), "myPassword12345");
        assertNotEquals(allUser.get(0).getEmail(), "myEmail2@blabla.com");
        assertEquals(2, allUser.size());
    }

    @Test
    void getUserDtoById() {
        UserDto getUserDto = userService.getUserDtoById(1L);

        verify(userRepository).findById(1L);
        assertEquals(getUserDto.getId(), 1);
        assertEquals(getUserDto.getUsername(), "myTestUsername");
        assertEquals(getUserDto.getPassword(), "myPassword123");
        assertNotEquals(getUserDto.getEmail(), "myEmail2@blabla.com");
    }

    @Test
    void getUserDtoByUsername() {
        UserDto getUserDto = userService.getUserDtoByUsername("myTestUsername2");

        verify(userRepository).findByUsername("myTestUsername2");
        assertEquals(getUserDto.getId(), 2);
        assertNotEquals(getUserDto.getUsername(), "myTestUsername");
        assertEquals(getUserDto.getPassword(), "myPassword12345");
        assertEquals(getUserDto.getEmail(), "myEmail2@blabla.com");
    }

    @Test
    void getUserDtoMinByUsername() {
        UserDtoMin getUserDto = userService.getUserDtoMinByUsername("myTestUsername");

        assertEquals(getUserDto.getId(), 1);
        assertEquals(getUserDto.getUsername(), "myTestUsername");
        assertNotEquals(getUserDto.getEmail(), "myEmail2@blabla.com");
    }

    @Test
    void getUserByUserInfoFullName() {
        when(userInfoRepository.findAll()).thenReturn(userInfoList);
        List<UserDto> getUser = userService.getUserByUserInfoFullName("JD");

        verify(userInfoRepository).findAll();
        assertEquals(getUser.get(0).getId(), 1);
        assertEquals(getUser.get(0).getUsername(), "myTestUsername");
        assertEquals(getUser.get(0).getPassword(), "myPassword123");
        assertNotEquals(getUser.get(0).getEmail(), "myEmail2@blabla.com");


    }

    @Test
    void createUser() {
        User newUser3 = new User("myTestUsername345", "myPassword99", "thirdEmail@something.com");
        UserInfo newUser3Info = new UserInfo();
        newUser3Info.setId(3L);
        newUser3Info.setUser(newUser3);
        newUser3.setId(3L);
        newUser3.setUserInfo(newUser3Info);
        UserDto newUser3Dto = userService.getUserDto(newUser3);
        when(userInfoRepository.findById(newUser3Dto.getUserInfoDtoMin().getId()))
                .thenReturn(Optional.of(newUser3Info));
        when(userRepository.findById(3L)).thenReturn(Optional.of(newUser3));
        userService.createUser(newUser3Dto);
        verify(userRepository).save(newUser3);

        User getUserById = userRepository.findById(3L).orElseThrow(() -> new RuntimeException("User Not found"));

        assertNotNull(getUserById);
        assertEquals(getUserById.getUsername(), "myTestUsername345");
        assertEquals(getUserById.getPassword(), "myPassword99");
        assertNotEquals(getUserById.getEmail(), "myEmail2@blabla.com");
    }

    @Test
    void deleteUser() {
        UserDto getUser2Dto = userService.getUserDto(newUser2);
        when(userInfoRepository.findById(getUser2Dto.getUserInfoDtoMin().getId())).thenReturn(Optional.of(newUserInfo2));

        userService.deleteUser(getUser2Dto);
        verify(userRepository).delete(newUser2);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<User> fetchUser = userRepository.findById(2L);

        assertFalse(fetchUser.isPresent());
    }
}