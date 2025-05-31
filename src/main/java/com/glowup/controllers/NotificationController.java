package com.glowup.controllers;

import com.glowup.model.Notification;
import com.glowup.model.User;
import com.glowup.services.NotificationService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/notifications/*")
public class NotificationController extends HttpServlet {
    private NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }

            User user = (User) session.getAttribute("user");
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<Notification> notifications = notificationService.getUserNotifications(user.getUserId(), false);
                int unreadCount = notificationService.getUnreadCount(user.getUserId());

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("unreadCount", unreadCount);

                JSONArray notificationsArray = new JSONArray();
                for (Notification notification : notifications) {
                    JSONObject notifObj = new JSONObject();
                    notifObj.put("notificationId", notification.getNotificationId());
                    notifObj.put("userId", notification.getUserId());
                    notifObj.put("title", notification.getTitle());
                    notifObj.put("message", notification.getMessage());
                    notifObj.put("type", notification.getType());
                    notifObj.put("relatedId", notification.getRelatedId());
                    notifObj.put("read", notification.isRead());
                    notifObj.put("createdAt", notification.getCreatedAt());
                    notificationsArray.put(notifObj);
                }
                jsonResponse.put("notifications", notificationsArray);

                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Endpoint not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }

            User user = (User) session.getAttribute("user");
            String pathInfo = request.getPathInfo();

            String requestBody = request.getReader().lines().collect(Collectors.joining());
            JSONObject json = new JSONObject(requestBody);

            if (pathInfo.equals("/mark-read")) {
                int notificationId = json.getInt("id");
                boolean success = notificationService.markAsRead(notificationId);
                response.getWriter().write("{\"success\":" + success + "}");

            } else if (pathInfo.equals("/mark-all-read")) {
                boolean success = notificationService.markAllAsRead(user.getUserId());
                response.getWriter().write("{\"success\":" + success + "}");

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Endpoint not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid request: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}