<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Notifications - GlowUp</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --primary-color: #6a0dad;
            --primary-light: #9c27b0;
            --primary-dark: #4a148c;
            --success-color: #4CAF50;
            --danger-color: #F44336;
            --warning-color: #FF9800;
            --text-light: #f3e5f5;
            --text-dark: #333;
            --bg-light: #f9f9f9;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
            color: var(--text-dark);
        }

        .notification-container {
            max-width: 800px;
            margin: 2rem auto;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .notification-header {
            background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
            color: white;
            padding: 1.5rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .notification-header h1 {
            margin: 0;
            font-size: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .badge {
            background: white;
            color: var(--primary-color);
            border-radius: 50%;
            width: 24px;
            height: 24px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 0.8rem;
            font-weight: bold;
        }

        .btn {
            background: white;
            color: var(--primary-color);
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 20px;
            cursor: pointer;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.3s;
        }

        .btn:hover {
            background: rgba(255,255,255,0.9);
            transform: translateY(-2px);
        }

        .notification-list {
            padding: 1rem;
        }

        .notification-item {
            padding: 1.5rem;
            margin: 0.5rem 0;
            border-radius: 8px;
            display: flex;
            gap: 1rem;
            align-items: flex-start;
            transition: all 0.3s;
            cursor: pointer;
            border-left: 4px solid transparent;
        }

        .notification-item:hover {
            background: var(--bg-light);
            transform: translateX(4px);
        }

        .notification-item.unread {
            background: var(--text-light);
            border-left-color: var(--primary-light);
        }

        .notification-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .notification-icon.accepted {
            background: rgba(76, 175, 80, 0.1);
            color: var(--success-color);
        }

        .notification-icon.rejected {
            background: rgba(244, 67, 54, 0.1);
            color: var(--danger-color);
        }

        .notification-icon.pending {
            background: rgba(255, 152, 0, 0.1);
            color: var(--warning-color);
        }

        .notification-content {
            flex-grow: 1;
        }

        .notification-title {
            font-weight: 600;
            margin: 0 0 0.5rem 0;
            display: flex;
            justify-content: space-between;
        }

        .notification-message {
            margin: 0;
            color: #666;
        }

        .notification-time {
            color: #999;
            font-size: 0.8rem;
            margin-top: 0.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .notification-status {
            padding: 0.2rem 0.5rem;
            border-radius: 4px;
            font-size: 0.8rem;
            font-weight: 600;
        }

        .status-accepted {
            background: rgba(76, 175, 80, 0.1);
            color: var(--success-color);
        }

        .status-rejected {
            background: rgba(244, 67, 54, 0.1);
            color: var(--danger-color);
        }

        .status-pending {
            background: rgba(255, 152, 0, 0.1);
            color: var(--warning-color);
        }

        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #999;
        }

        .empty-state i {
            font-size: 3rem;
            margin-bottom: 1rem;
            color: #ddd;
        }

        .test-data-banner {
            background: var(--warning-color);
            color: white;
            padding: 0.5rem;
            text-align: center;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
<div class="notification-container">
    <div class="notification-header">
        <h1>
            <i class="fas fa-bell"></i>
            Notifications
            <c:if test="${unreadCount > 0}">
                <span class="badge">${unreadCount}</span>
            </c:if>
        </h1>
        <button id="mark-all-read" class="btn">
            <i class="fas fa-check-double"></i> Mark All as Read
        </button>
    </div>

    <c:if test="${param.testdata == 'true'}">
        <div class="test-data-banner">
            <i class="fas fa-flask"></i> Showing test notifications - will not appear in production
        </div>
    </c:if>

    <div class="notification-list">
        <c:choose>
            <c:when test="${not empty notifications}">
                <c:forEach var="notification" items="${notifications}">
                    <div class="notification-item ${notification.read ? '' : 'unread'}"
                         data-id="${notification.notificationId}">

                        <c:choose>
                            <c:when test="${fn:contains(notification.title, 'ACCEPTED')}">
                                <div class="notification-icon accepted">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                            </c:when>
                            <c:when test="${fn:contains(notification.title, 'REJECTED')}">
                                <div class="notification-icon rejected">
                                    <i class="fas fa-times-circle"></i>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="notification-icon pending">
                                    <i class="fas fa-clock"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <div class="notification-content">
                            <div class="notification-title">
                                <span>${notification.title}</span>
                                <c:choose>
                                    <c:when test="${fn:contains(notification.title, 'ACCEPTED')}">
                                        <span class="notification-status status-accepted">Accepted</span>
                                    </c:when>
                                    <c:when test="${fn:contains(notification.title, 'REJECTED')}">
                                        <span class="notification-status status-rejected">Rejected</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="notification-status status-pending">Pending</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <p class="notification-message">${notification.message}</p>
                            <div class="notification-time">
                                <i class="far fa-clock"></i>
                                <fmt:formatDate value="${notification.createdAt}"
                                                pattern="MMM dd, yyyy 'at' hh:mm a"/>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <i class="fas fa-bell-slash"></i>
                    <p>No notifications yet</p>
                    <a href="?testdata=true" class="btn" style="margin-top: 1rem;">
                        <i class="fas fa-flask"></i> Load Test Notifications
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    // Mark notification as read when clicked
    document.querySelectorAll('.notification-item').forEach(item => {
        item.addEventListener('click', function() {
            const notificationId = this.getAttribute('data-id');

            if (this.classList.contains('unread')) {
                fetch('/notifications/mark-read', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ id: notificationId })
                }).then(response => {
                    if (response.ok) {
                        this.classList.remove('unread');
                        updateUnreadCount();
                    }
                });
            }
        });
    });

    // Mark all as read
    document.getElementById('mark-all-read').addEventListener('click', function() {
        fetch('/notifications/mark-all-read', {
            method: 'POST'
        }).then(response => {
            if (response.ok) {
                document.querySelectorAll('.notification-item').forEach(item => {
                    item.classList.remove('unread');
                });
                updateUnreadCount();
            }
        });
    });

    // Update unread count badge
    function updateUnreadCount() {
        fetch('/notifications/unread')
            .then(response => response.json())
            .then(data => {
                const badge = document.querySelector('.badge');
                if (data.count > 0) {
                    badge.textContent = data.count;
                    badge.style.display = 'inline-flex';
                } else {
                    badge.style.display = 'none';
                }
            });
    }

    // Poll for new notifications every 30 seconds
    setInterval(updateUnreadCount, 30000);
</script>
</body>
</html>