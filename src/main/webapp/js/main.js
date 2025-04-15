// Mobile Menu Toggle
document.addEventListener('DOMContentLoaded', function() {
    const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');
    const mainNav = document.querySelector('.main-nav');
    const authButtons = document.querySelector('.auth-buttons');

    if (mobileMenuToggle) {
        mobileMenuToggle.addEventListener('click', function() {
            // Toggle mobile menu visibility
            const isNavVisible = mainNav.style.display === 'block';
            mainNav.style.display = isNavVisible ? 'none' : 'block';
            authButtons.style.display = isNavVisible ? 'none' : 'block';

            // Toggle menu icon
            const icon = mobileMenuToggle.querySelector('i');
            if (icon) {
                icon.className = isNavVisible ? 'fas fa-bars' : 'fas fa-times';
            }
        });
    }

    // Adjust for window resize
    window.addEventListener('resize', function() {
        if (window.innerWidth > 768) {
            // Reset to default styles on larger screens
            if (mainNav) mainNav.style.display = '';
            if (authButtons) authButtons.style.display = '';

            // Reset menu icon if exists
            const icon = mobileMenuToggle?.querySelector('i');
            if (icon) icon.className = 'fas fa-bars';
        } else {
            // Ensure mobile menu is hidden by default on small screens
            if (mainNav) mainNav.style.display = 'none';
            if (authButtons) authButtons.style.display = 'none';
        }
    });

    // Initialize mobile menu state
    if (window.innerWidth <= 768) {
        if (mainNav) mainNav.style.display = 'none';
        if (authButtons) authButtons.style.display = 'none';
    }
});