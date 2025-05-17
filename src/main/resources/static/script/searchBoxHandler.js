document.addEventListener('DOMContentLoaded', function() {
    const searchBox = document.getElementById('search-input');
    const resultsList = document.getElementById('search-results');

    searchBox.addEventListener('input', function() {
        const keyword = searchBox.value;
        const username = document.getElementById('usernameField').value;
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        if (keyword.length === 0) {
            resultsList.innerHTML = '';
            resultsList.style.display = 'none';
            return;
        }

        fetch(`/api/user/search?query=${encodeURIComponent(keyword)}&username=${encodeURIComponent(username)}`)
            .then(response => response.json())
            .then(data => {
                resultsList.innerHTML = '';
                data.forEach(item => {
                    const li = document.createElement('li');
                    li.textContent = item.username; // Adjust if using objects
                    resultsList.style.display = 'block';
                    li.addEventListener('click', () => {
                        // Example: sending username to server with fetch POST
                        fetch('/api/user/send_request', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                                [csrfHeader]: csrfToken
                            },
                            body: JSON.stringify({ senderUsername: username,
                            receiverUsername: item.username})
                        })
                            .then(res => res.json())
                            .then(result => {
                                console.log('Server response:', result);
                            });

                        // Optional: update UI
                        searchBox.value = item.username; // fill the input with selection
                        resultsList.innerHTML = ''; // clear the dropdown
                    });

                    resultsList.appendChild(li);
                });
            });
    });
});