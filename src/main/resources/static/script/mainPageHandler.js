
let stompClient = null;
let currentSubscription = null;

function connect(event) {
    if (stompClient !== null) {
        if (currentSubscription) {
            currentSubscription.unsubscribe();
            currentSubscription = null;
        }

        stompClient.disconnect(() => {
            console.log("Previous connection closed.");
            startNewConnection();
        });
    } else {
        startNewConnection();
    }
    /*const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/chatroom/' + chatRoomId, function (message) {
            showMessage(JSON.parse(message.body));
        });

    });
    const messageArea = document.getElementById('chat-display');
    messageArea.scrollTop = messageArea.scrollHeight;*/
}


function startNewConnection() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        currentSubscription = stompClient.subscribe('/topic/chatroom/' + chatRoomId, function (message) {
            showMessage(JSON.parse(message.body));
        });
    });

    const messageArea = document.getElementById('chat-display');
    messageArea.scrollTop = messageArea.scrollHeight;
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
    const isSender = message.chatRoomMemberDtoMin.id === senderId;
    const senderUsername = message.chatRoomMemberDtoMin.memberName;

    // Wrapper for alignment
    const wrapper = document.createElement('div');
    wrapper.className = `message-wrapper ${isSender ? 'sender' : 'receiver'}`;

    const messageElement = document.createElement('div');
    if (isSender) {
        messageElement.className = 'chat-message right';
    } else {
        messageElement.className = 'chat-message left';
    }

    const header = document.createElement('div');
    header.className = 'chat-header';

    const profileImg = document.createElement('img');
    profileImg.className = 'profile-img';
    profileImg.src = `/api/user/edit/image/${senderUsername}`;
    profileImg.alt = 'N/A';

    const getUsername = document.createElement('p');
    getUsername.className = 'username';
    getUsername.textContent = senderUsername;

    header.appendChild(profileImg);
    header.appendChild(getUsername);

    const body = document.createElement('div');
    body.className = 'chat-body';

    const messageText = document.createElement('span');
    messageText.textContent = message.contents;

    body.appendChild(messageText);
    messageElement.appendChild(header);
    messageElement.appendChild(body);
    wrapper.appendChild(messageElement);
    messageArea.appendChild(wrapper);


    messageArea.scrollTop = messageArea.scrollHeight;
}

window.onload = function () {
    if (chatRoomId !== 0 && senderId !== 0) {
        connect();
    }
}