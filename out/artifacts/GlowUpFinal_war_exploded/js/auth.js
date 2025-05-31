// Handle user type toggle
document.addEventListener('DOMContentLoaded', function() {
    const userTypeBtns = document.querySelectorAll('.user-type-btn');
    const userTypeField = document.getElementById('userTypeField');

    userTypeBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            userTypeBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            userTypeField.value = this.dataset.type;
        });
    });

    // Handle login/register errors
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('error')) {
        let errorMsg = 'An error occurred. Please try again.';
        if (urlParams.get('error') === '1') {
            errorMsg = 'Invalid email or password.';
        }
        showError(errorMsg);
    }

    if (urlParams.has('success') || urlParams.has('registered')) {
        showSuccess('Registration successful! Please login.');
    }
});

function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'alert alert-error';
    errorDiv.textContent = message;

    const authContainer = document.querySelector('.auth-container');
    authContainer.insertBefore(errorDiv, authContainer.firstChild);

    setTimeout(() => {
        errorDiv.remove();
    }, 5000);
}

function showSuccess(message) {
    const successDiv = document.createElement('div');
    successDiv.className = 'alert alert-success';
    successDiv.textContent = message;

    const authContainer = document.querySelector('.auth-container');
    authContainer.insertBefore(successDiv, authContainer.firstChild);

    setTimeout(() => {
        successDiv.remove();
    }, 5000);
}