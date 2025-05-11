package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDto;
import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDtoMin;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChatRoomMemberServiceTest {

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
    ChatRoom chatRoom = new ChatRoom(newUserInfo.getFullName() + " " + newUserInfo2.getFullName(), dateTimeChatRoom, ChatRoomTypes.PRIVATE);

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


        chatRoom.setId(id1);
        chatRoomMember1.setId(id1);
        chatRoomMember2.setId(id2);
        chatRoomMember1.setMessages(new ArrayList<>());
        chatRoomMember2.setMessages(new ArrayList<>());
        chatRoom.setChatRoomMembers(chatRoomMemberList);
        chatRoom.setMessages(new ArrayList<>());

        messageService = new MessageService(messageRepository, chatRoomRepository, chatRoomMemberRepository);
        chatRoomMemberService = new ChatRoomMemberService(userRepository, chatRoomRepository, messageRepository, chatRoomMemberRepository);
        chatRoomService = new ChatRoomService(chatRoomRepository, chatRoomMemberRepository, messageRepository, userRepository);



        when(userRepository.saveAll(anyList())).thenReturn(userLists);
        when(userRepository.findAll()).thenReturn(userLists);
        when(userInfoRepository.saveAll(anyList())).thenReturn(userInfoList);
        when(userRelationshipRepository.saveAll(anyList())).thenReturn(userRelationshipLists);

        when(chatRoomRepository.save(any())).thenReturn(chatRoom);
        when(chatRoomRepository.getReferenceById(1L)).thenReturn(chatRoom);
        when(chatRoomMemberRepository.saveAll(anyList())).thenReturn(chatRoomMemberList);
    }

    @Test
    void getChatMemberDto() {
        ChatRoomMemberDto chatRoomMemberDto = chatRoomMemberService.getChatMemberDto(chatRoomMember1);

        assertThat(chatRoomMemberDto).isNotNull();
        assertEquals(chatRoomMemberDto.getUserDtoMin().getUsername(), "myTestUsername");
        assertEquals(chatRoomMemberDto.getChatRoomDtoMin().getName(), "JD AP");
        assertNotEquals(chatRoomMemberDto.getId(), 2L);
        assertNotEquals(chatRoomMemberDto.getRoles(), Roles.ADMIN);
    }

    @Test
    void getChatMemberDtoMin() {
        ChatRoomMemberDtoMin chatRoomMemberDtoMin = chatRoomMemberService.getChatMemberDtoMin(chatRoomMember2);

        assertThat(chatRoomMemberDtoMin).isNotNull();
        assertEquals(2L, chatRoomMemberDtoMin.getId());
        assertEquals(Roles.MEMBER , chatRoomMemberDtoMin.getRoles());
    }

    @Test
    void getChatRoomMemberDtoMiniById() {
        when(chatRoomMemberRepository.getReferenceById(1L)).thenReturn(chatRoomMember1);
        ChatRoomMemberDtoMin chatRoomMemberDtoMin = chatRoomMemberService.getChatRoomMemberDtoMiniById(1L);

        verify(chatRoomMemberRepository).getReferenceById(1L);
        assertThat(chatRoomMemberDtoMin).isNotNull();
        assertNotEquals(chatRoomMemberDtoMin.getId(), 2L);
        assertNotEquals(chatRoomMemberDtoMin.getRoles(), Roles.ADMIN);
    }

    @Test
    void getChatRoomMemberFromDtoMini() {
        ChatRoomMemberDtoMin chatRoomMemberDtoMin = chatRoomMemberService.getChatMemberDtoMin(chatRoomMember2);
        when(chatRoomMemberRepository.getReferenceById(2L)).thenReturn(chatRoomMember2);

        ChatRoomMember chatRoomMember = chatRoomMemberService.getChatRoomMemberFromDtoMini(chatRoomMemberDtoMin);

        verify(chatRoomMemberRepository).getReferenceById(2L);
        assertThat(chatRoomMember).isNotNull();
        assertEquals(2L, chatRoomMember.getId());
        assertEquals("myTestUsername2", chatRoomMember.getUser().getUsername());
        assertEquals("JD AP", chatRoomMember.getChatRoom().getName());
        assertNotEquals(Roles.ADMIN, chatRoomMember.getRoles());
    }
}