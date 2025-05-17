

function showPopup() {
    document.getElementById("addGroupChatForm").style.display = "block";
}

function hidePopup() {
    document.getElementById("addGroupChatForm").style.display = "none";
}

const selectedFriendIds = new Set();

function addFriend(button) {
    const li = button.parentElement;
    const id = li.getAttribute("data-id");
    const username = li.getAttribute("data-username");

    if (!selectedFriendIds.has(id)) {
        selectedFriendIds.add(id);

        const selectedList = document.getElementById("selectedFriends");
        const newItem = document.createElement("li");
        newItem.textContent = username + " ";

        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Remove";
        removeBtn.onclick = () => {
            selectedFriendIds.delete(id);
            selectedList.removeChild(newItem);
        };

        newItem.appendChild(removeBtn);
        selectedList.appendChild(newItem);
    }
}

function prepareFormBeforeSubmit() {
    document.getElementById("memberIds").value = Array.from(selectedFriendIds).join(",");
    return true;
}

function filterFriends() {
    const search = document.getElementById("friendSearch").value.toLowerCase();
    const friends = document.querySelectorAll("#friendList ul li");

    friends.forEach(friend => {
        const name = friend.getAttribute("data-username").toLowerCase();
        friend.style.display = name.includes(search) ? "list-item" : "none";
    });
}