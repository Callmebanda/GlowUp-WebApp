document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
        });

        if (response.redirected) {
            window.location.href = response.url;
        } else {
            const message = await response.text();
            document.getElementById('message').textContent = message;
        }
    } catch (error) {
        console.error('Error:', error);
    }
});