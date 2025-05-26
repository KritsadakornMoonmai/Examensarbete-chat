
function fetchList() {
    var userId = document.getElementById('user-chat-id').value;
    var chatRoomId = document.getElementById('chat-id').value;

    fetch(`/api/chatroom/invite?userId=${userId}&chatRoomId=${chatRoomId}`, {
        method: "GET"
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch');
            return response.json();
        })
        .then(data => {
            const container = document.getElementById('friendsListContainer');
            container.innerHTML = '';

            if (data.length === 0) {
                container.innerHTML = '<p>No friends available to invite.</p>';
                return;
            }

            data.forEach(friend => {
                const btn = document.createElement('button');
                btn.textContent = `Invite ${friend.username}`;
                btn.className = 'invite-button';
                btn.onclick = () => inviteUserToChat(friend.id, chatRoomId, friend.username);
                container.appendChild(btn);
            });

            showPopup('addMemberForm');
        })
        .catch(error => {
            console.error('Error fetching friend list:', error);
        });
}

function inviteUserToChat(userId, chatRoomId, username) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    fetch(`/api/chatroom/invite`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({
            chatRoomId: chatRoomId,
            friendId: userId,
            friendUsername: username
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to invite user');
            }
            return response.text();
        })
        .then(result => {
            alert('User invited successfully!');
        })
        .catch(error => {
            console.error('Error inviting user:', error);
        });
}