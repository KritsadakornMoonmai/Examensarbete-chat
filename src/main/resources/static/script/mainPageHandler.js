
let stompClient = null;
let chatRoomId = /*[[${chatRoom.getId()}]]*/ 1;
let senderId = /*[[${sender.getId()}]]*/ 1;
let username = /*[['${sender.getUserDtoMin().getUsername()'}]]*/ 'username';

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chatroom/' + chatRoomId, function (message) {
            showMessage(JSON.parse(message.body));
        });
    });
}

function sendMessage() {
    const content = document.getElementById('chat-input').value;
    if (content && stompClient) {
        const chatMessage = {
            contents: content,
            chatRoomDtoMin: { id: chatRoomId },
            chatRoomMemberDtoMin: { id: senderId }
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        document.getElementById('chat-input').value = '';
    }
}

function showMessage(message) {
    const messageArea = document.getElementById('chat-display');
    const messageElement = document.createElement('div');

    const isSender = message.chatRoomMemberDtoMin.id === senderId;
    const senderUsername = isSender? username : 'Username placeholder';

    messageElement.textContent = senderUsername + ": " + message.contents;


    //messageElement.textContent = message.chatRoomMemberDtoMin.username + ": " + message.contents;

    if (isSender) {
        messageElement.style.textAlign = 'right';
        messageElement.style.backgroundColor = '#dcf8c6';
    } else {
        messageElement.style.textAlign = 'left';
        messageElement.style.backgroundColor = '#e1f0ff';
    }

    messageElement.style.padding = '10px';
    messageElement.style.margin = '5px';
    messageElement.style.borderRadius = '10px';
    messageArea.appendChild(messageElement);
}

window.onload = connect;