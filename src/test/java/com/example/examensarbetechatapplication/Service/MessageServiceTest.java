package com.example.examensarbetechatapplication.Service;

import com.example.examensarbetechatapplication.DTO.ChatRoomDtoMin;
import com.example.examensarbetechatapplication.DTO.ChatRoomMemberDtoMin;
import com.example.examensarbetechatapplication.DTO.MessageDto;
import com.example.examensarbetechatapplication.DTO.MessageDtoMin;
import com.example.examensarbetechatapplication.Model.*;
import com.example.examensarbetechatapplication.Repository.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

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

    //MESSAGES---------------------------------------------------
    LocalDateTime dateTimeMessage1 = LocalDateTime.of(2025, 2, 11, 13, 11, 21);
    LocalDateTime dateTimeMessage2 = LocalDateTime.of(2025, 2, 11, 13, 14, 21);

    Message message1 = new Message("How are you?", dateTimeMessage1, chatRoomMember1, chatRoom);
    Message message2 = new Message("I am good.", dateTimeMessage2, chatRoomMember2, chatRoom);

    List<Message> messageList = List.of(message1, message2);


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
        message1.setId(id1);
        message2.setId(id2);

        messageService = new MessageService(messageRepository, chatRoomRepository, chatRoomMemberRepository);
        chatRoomMemberService = new ChatRoomMemberService(userRepository, chatRoomRepository, messageRepository, chatRoomMemberRepository);
        chatRoomService = new ChatRoomService(chatRoomRepository,chatRoomMemberRepository, messageRepository, userRepository);



        when(userRepository.saveAll(anyList())).thenReturn(userLists);
        when(userRepository.findAll()).thenReturn(userLists);
        when(userInfoRepository.saveAll(anyList())).thenReturn(userInfoList);
        when(userRelationshipRepository.saveAll(anyList())).thenReturn(userRelationshipLists);

        when(chatRoomRepository.save(any())).thenReturn(chatRoom);
        when(chatRoomMemberRepository.saveAll(anyList())).thenReturn(chatRoomMemberList);
        when(messageRepository.saveAll(anyList())).thenReturn(messageList);
    }

    @Test
    void getMessageDto() {
        MessageDto messageDto = messageService.getMessageDto(message1);

        assertThat(messageDto).isNotNull();
        assertEquals(messageDto.getContents(), "How are you?");
        assertEquals(messageDto.getTime(), dateTimeMessage1);
        assertNotEquals(messageDto.getId(), 2L);
    }

    @Test
    void getMessageDtoMin() {
        MessageDtoMin messageDtoMin = messageService.getMessageDtoMin(message2);

        assertThat(messageDtoMin).isNotNull();
        assertEquals(messageDtoMin.getContents(), "I am good.");
        assertEquals(messageDtoMin.getTime(), dateTimeMessage2);
        assertNotEquals(messageDtoMin.getId(), 1L);
    }

    @Test
    void getMessageFromDto() {
        MessageDto messageDto = messageService.getMessageDto(message1);

        assertThat(messageDto).isNotNull();
        assertEquals(messageDto.getContents(), "How are you?");
        assertEquals(messageDto.getTime(), dateTimeMessage1);
        assertNotEquals(messageDto.getId(), 2L);
    }

    @Test
    void getMessageHandler() {
        when(chatRoomMemberRepository.getReferenceById(2L)).thenReturn(chatRoomMember2);
        when(chatRoomRepository.getReferenceById(1L)).thenReturn(chatRoom);
        ChatRoomDtoMin chatRoomDtoMin = chatRoomService.getChatRoomDtoMiniById(1L);
        verify(chatRoomRepository).getReferenceById(1L);
        ChatRoomMemberDtoMin chatRoomMemberDtoMin = chatRoomMemberService.getChatRoomMemberDtoMiniById(2L);
        verify(chatRoomMemberRepository).getReferenceById(2L);

        when(messageRepository.getMessageByChatRoomMemberAndChatRoom(chatRoomMember2, chatRoom)).thenReturn(message2);
        MessageDto messageDto = messageService.getMessageHandler(chatRoomMemberDtoMin, chatRoomDtoMin);

        verify(messageRepository).getMessageByChatRoomMemberAndChatRoom(chatRoomMember2, chatRoom);

        assertThat(messageDto).isNotNull();
        assertEquals(messageDto.getContents(), "I am good.");
        assertEquals(messageDto.getTime(), dateTimeMessage2);
        assertEquals(messageDto.getChatRoomDtoMin(), chatRoomDtoMin);
        assertNotEquals(messageDto.getId(), 1L);
    }

    @Test
    void saveMessage() {
        LocalDateTime dateTimeMessage3 = LocalDateTime.of(2025, 2, 11, 13, 20, 56);
        Message message3 = new Message("Good to hear!", dateTimeMessage3, chatRoomMember1, chatRoom);
        message3.setId(3L);
        MessageDto messageDto = messageService.getMessageDto(message3);
        when(chatRoomMemberRepository.getReferenceById(1L)).thenReturn(chatRoomMember1);
        when(chatRoomRepository.getReferenceById(1L)).thenReturn(chatRoom);
        messageService.saveMessage(messageDto);

        verify(messageRepository).save(message3);

        when(messageRepository.findById(3L)).thenReturn(Optional.of(message3));
        Message fetchMessage = messageRepository.findById(3L).orElseThrow(() -> new RuntimeException("Message not found"));

        verify(messageRepository).findById(3L);

        System.out.println("Test process");
        assertThat(fetchMessage).isNotNull();
        assertEquals(fetchMessage.getContents(), "Good to hear!");
        assertEquals(fetchMessage.getTime(), dateTimeMessage3);
        assertEquals(fetchMessage.getChatRoom(), chatRoom);
        assertNotEquals(fetchMessage.getChatRoomMember(), chatRoomMember2);
    }
}