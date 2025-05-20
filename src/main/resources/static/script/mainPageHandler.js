
let stompClient = null;


function connect(event) {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chatroom/' + chatRoomId, function (message) {
            showMessage(JSON.parse(message.body));
        });

    });
}

function sendMessage(event) {
    event.preventDefault();
    const content = document.getElementById('chat-input').value;
    if (content && stompClient) {
        const chatMessage = {
            contents: content,
            chatRoomDtoMin: { id: chatRoomId },
            chatRoomMemberDtoMin: { id: senderId, memberName: username }
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        document.getElementById('chat-input').value = '';
    }
}



function showMessage(message) {
    console.log('Showed: chatRoomId:', chatRoomId, 'senderId:', senderId, 'username:', username);
    const messageArea = document.getElementById('chat-display');
    const messageElement = document.createElement('div');

    const isSender = message.chatRoomMemberDtoMin.id === senderId;
    //const senderUsername = isSender? username : friendUsername;
    const senderUsername = message.chatRoomMemberDtoMin.memberName;

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
    messageElement.style.color= 'black';
    messageArea.appendChild(messageElement);

    messageArea.scrollTop = messageArea.scrollHeight;
}

window.onload = function () {
    if (chatRoomId !== 0 && senderId !== 0) {
        connect();
    }
}