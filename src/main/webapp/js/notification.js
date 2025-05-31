class NotificationSystem {
    constructor() {
        this.notificationBell = document.getElementById('notificationBell');
        this.notificationCount = document.getElementById('notificationCount');
        this.notificationDropdown = document.getElementById('notificationDropdown');
        this.userId = null; // Should be set from session

        this.init();
    }

    init() {
        if (this.notificationBell) {
            this.notificationBell.addEventListener('click', this.toggleDropdown.bind(this));
            this.checkForNewNotifications();
            setInterval(this.checkForNewNotifications.bind(this), 30000); // Check every 30 seconds
        }
    }

    toggleDropdown() {
        if (this.notificationDropdown.style.display === 'block') {
            this.notificationDropdown.style.display = 'none';
        } else {
            this.loadNotifications();
            this.notificationDropdown.style.display = 'block';
        }
    }

    checkForNewNotifications() {
        if (!this.userId) return;

        fetch(`/api/notifications/unread-count?userId=${this.userId}`)
            .then(response => response.json())
            .then(data => {
                if (data.count > 0) {
                    this.notificationCount.textContent = data.count;
                    this.notificationCount.style.display = 'inline-block';

                    // Optional: Show desktop notification
                    if (data.count > 0 && Notification.permission === "granted") {
                        new Notification(`You have ${data.count} new notifications`);
                    }
                } else {
                    this.notificationCount.style.display = 'none';
                }
            });
    }

    loadNotifications() {
        fetch(`/api/notifications?userId=${this.userId}`)
            .then(response => response.json())
            .then(notifications => {
                this.renderNotifications(notifications);
            });
    }

    renderNotifications(notifications) {
        const container = document.createElement('div');
        container.className = 'notifications-container';

        if (notifications.length === 0) {
            container.innerHTML = '<div class="no-notifications">No notifications</div>';
        } else {
            notifications.forEach(notification => {
                const notificationElement = document.createElement('div');
                notificationElement.className = `notification ${notification.is_read ? 'read' : 'unread'}`;
                notificationElement.innerHTML = `
                    <div class="notification-header">
                        <h4>${notification.title}</h4>
                        <span class="notification-time">${this.formatTime(notification.created_at)}</span>
                    </div>
                    <p>${notification.message}</p>
                    ${notification.related_id ? `<a href="/appointment-details?id=${notification.related_id}" class="view-details">View Details</a>` : ''}
                `;

                notificationElement.addEventListener('click', () => {
                    this.markAsRead(notification.notification_id);
                    if (notification.related_id) {
                        window.location.href = `/appointment-details?id=${notification.related_id}`;
                    }
                });

                container.appendChild(notificationElement);
            });
        }

        this.notificationDropdown.innerHTML = '';
        this.notificationDropdown.appendChild(container);
    }

    markAsRead(notificationId) {
        fetch(`/api/notifications/mark-read`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ notificationId })
        });
    }

    formatTime(timestamp) {
        // Implement your time formatting logic
        return new Date(timestamp).toLocaleTimeString();
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    // Get userId from your authentication system
    const userId = document.body.getAttribute('data-user-id'); // Example
    if (userId) {
        const notificationSystem = new NotificationSystem();
        notificationSystem.userId = userId;

        // Request notification permission
        if ('Notification' in window && Notification.permission !== "granted") {
            Notification.requestPermission();
        }
    }
});