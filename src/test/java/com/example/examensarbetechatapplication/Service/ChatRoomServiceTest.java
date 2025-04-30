package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.ChatRoomDto;
import com.example.examensarbetechatapplication.DTO.ChatRoomDtoMin;
import com.example.examensarbetechatapplication.Model.*;
import com.example.examensarbetechatapplication.Repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChatRoomServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private UserRelationshipRepository userRelationshipRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Autowired
    @InjectMocks
    private MessageService messageService;
    @Autowired
    @InjectMocks
    private ChatRoomService chatRoomService;
    @Autowired
    @InjectMocks
    private ChatRoomMemberService chatRoomMemberService;

    long id1 = 1L;
    long id2 = 2L;

    //USERS-------------------------------------------------

    User newUser1 = new User("myTestUsername", "myPassword123", "myEmail@blabla.com");
    User newUser2 = new User("myTestUsername2", "myPassword12345", "myEmail2@blabla.com");
    //USERINFO----------------------------------------------
    UserInfo newUserInfo = new UserInfo("JD", 30, "123456", null, newUser1);
    UserInfo newUserInfo2 = new UserInfo("AP", 22, "52151", null, newUser2);

    //USERRELATIONSHIPS-------------------------------------
    LocalDateTime localDateTime = LocalDateTime.of(2025, 2, 11, 12, 11, 25);

    RelationshipStatus pending = RelationshipStatus.ACCEPTED;

    UserRelationship relationship1 = new UserRelationship(1L, localDateTime, pending, newUser1, newUser2);
    UserRelationship relationship2 = new UserRelationship(2L, localDateTime, pending, newUser2, newUser1);

    List<UserRelationship> user1Initiated = List.of(relationship1);
    List<UserRelationship> user2Received = List.of(relationship2);

    List<User> userLists = List.of(newUser1, newUser2);

    List<UserInfo> userInfoList = List.of(newUserInfo, newUserInfo2);

    List<UserRelationship> userRelationshipLists = List.of(relationship1, relationship2);

    //CHATROOM--------------------------------------------------
    LocalDateTime dateTimeChatRoom = LocalDateTime.of(2025, 2, 11, 13, 10, 54);
    ChatRoom chatRoom;

    //CHATROOM MEMBERS-------------------------------------------
    ChatRoomMember chatRoomMember1 = new ChatRoomMember(newUser1, dateTimeChatRoom, chatRoom, Roles.MEMBER);
    ChatRoomMember chatRoomMember2 = new ChatRoomMember(newUser2, dateTimeChatRoom, chatRoom, Roles.MEMBER);

    List<ChatRoomMember> chatRoomMemberList = List.of(chatRoomMember1, chatRoomMember2);



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
        chatRoom = new ChatRoom(newUser1.getUserInfo().getFullName() + " " + newUser2.getUserInfo().getFullName(), dateTimeChatRoom);

        chatRoom.setId(id1);
        chatRoomMember1.setId(id1);
        chatRoomMember2.setId(id2);
        chatRoomMember1.setMessages(new ArrayList<>());
        chatRoomMember2.setMessages(new ArrayList<>());
        chatRoom.setChatRoomMembers(chatRoomMemberList);
        chatRoom.setMessages(new ArrayList<>());

        messageService = new MessageService(messageRepository, chatRoomRepository, chatRoomMemberRepository);
        chatRoomMemberService = new ChatRoomMemberService(userRepository, chatRoomRepository, messageRepository, chatRoomMemberRepository);
        chatRoomService = new ChatRoomService(chatRoomRepository, chatRoomMemberRepository, messageRepository);



        when(userRepository.saveAll(anyList())).thenReturn(userLists);
        when(userRepository.findAll()).thenReturn(userLists);
        when(userInfoRepository.saveAll(anyList())).thenReturn(userInfoList);
        when(userRelationshipRepository.saveAll(anyList())).thenReturn(userRelationshipLists);

        when(chatRoomRepository.save(any())).thenReturn(chatRoom);
        when(chatRoomRepository.getReferenceById(1L)).thenReturn(chatRoom);
        when(chatRoomMemberRepository.saveAll(anyList())).thenReturn(chatRoomMemberList);
    }

    @Test
    void getChatRoomDto() {
        ChatRoomDto getChatRoomDto = chatRoomService.getChatRoomDto(chatRoom);

        assertThat(getChatRoomDto).isNotNull();
        assertEquals(getChatRoomDto.getName(), "JD AP");
        assertEquals(getChatRoomDto.getId(), 1L);
        assertEquals(getChatRoomDto.getChatRoomMemberDtoMins().get(0).getJoinedAt(), dateTimeChatRoom);
        assertNotEquals(getChatRoomDto.getCreateAt(), localDateTime);
    }

    @Test
    void getChatRoomDtoMin() {
        ChatRoomDtoMin getChatRoomDtoMin = chatRoomService.getChatRoomDtoMin(chatRoom);

        assertThat(getChatRoomDtoMin).isNotNull();
        assertEquals(getChatRoomDtoMin.getName(), "JD AP");
        assertEquals(getChatRoomDtoMin.getId(), 1L);
        assertNotEquals(getChatRoomDtoMin.getCreateAt(), localDateTime);
    }

    @Test
    void getChatRoomDtoMiniById() {
        ChatRoomDtoMin getChatRoomDtoMin = chatRoomService.getChatRoomDtoMiniById(1L);

        assertThat(getChatRoomDtoMin).isNotNull();
        assertEquals(getChatRoomDtoMin.getName(), "JD AP");
        assertEquals(getChatRoomDtoMin.getId(), 1L);
        assertNotEquals(getChatRoomDtoMin.getCreateAt(), localDateTime);
    }

    @Test
    void saveChatRoom() {
        LocalDateTime dateTimeChatRoom2 = LocalDateTime.of(2025, 2, 12, 9, 34, 8);
        ChatRoom chatRoom2 = new ChatRoom("ChatRooms", dateTimeChatRoom2);
        chatRoom2.setId(2L);

        ChatRoomMember chatRoomMember3 = new ChatRoomMember(newUser1, dateTimeChatRoom2, chatRoom2, Roles.ADMIN);
        ChatRoomMember chatRoomMember4 = new ChatRoomMember(newUser2, dateTimeChatRoom2, chatRoom2, Roles.MEMBER);
        chatRoomMember3.setId(3L);
        chatRoomMember4.setId(4L);
        List<ChatRoomMember> chatRoomMemberList2 = List.of(chatRoomMember3 , chatRoomMember4);
        chatRoom2.setChatRoomMembers(chatRoomMemberList2);


        when(chatRoomMemberRepository.saveAll(anyList())).thenReturn(chatRoomMemberList2);
        //chatRoom2.setChatRoomMembers(new ArrayList<>());

        System.out.println("Before all save");
        ChatRoomDto chatRoomDto = chatRoomService.getChatRoomDto(chatRoom2);
        assertThat(chatRoomDto).isNotNull();

        System.out.println("save 1");
        when(chatRoomRepository.getReferenceById(2L)).thenReturn(chatRoom2);
        when(chatRoomMemberRepository.getReferenceById(3L)).thenReturn(chatRoomMember3);
        when(chatRoomMemberRepository.getReferenceById(4L)).thenReturn(chatRoomMember4);
        when(messageRepository.findById(any())).thenReturn(null);
        when(chatRoomRepository.save(any())).thenReturn(chatRoom2);

        chatRoomService.saveChatRoom(chatRoomDto);

        System.out.println("save 2");
        verify(chatRoomRepository).save(chatRoom2);
        System.out.println("save 2.1");
        verify(chatRoomMemberRepository).getReferenceById(3L);
        verify(chatRoomMemberRepository).getReferenceById(4L);
        System.out.println("save 2.2");
        System.out.println("save 2.3");

        when(chatRoomRepository.findById(2L)).thenReturn(Optional.of(chatRoom2));
        System.out.println("save 2.4");
        ChatRoom getChatRoom = chatRoomRepository.findById(2L).orElseThrow(() -> new RuntimeException("ChatRoom not found"));
        System.out.println("save 3");

        when(chatRoomRepository.findAll()).thenReturn(List.of(chatRoom, chatRoom2));
        assertThat(getChatRoom).isNotNull();
        assertEquals(2, chatRoomRepository.findAll().size());
        assertEquals(getChatRoom.getName(), "ChatRooms");
        assertEquals(getChatRoom.getId(), 2L);
        assertNotEquals(getChatRoom.getCreateAt(), localDateTime);
        System.out.println("Assertion done");
    }
}