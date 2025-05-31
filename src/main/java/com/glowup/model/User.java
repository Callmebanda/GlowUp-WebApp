package com.glowup.model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String email;
    private String password;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private String name;
    private String phone;
    private String address;
    private String profilePicture;
    private String userType;
    private boolean isActive;

    // Constructors, Getters and Setters
    public User() {}

    public User(String email, String password, String name, String phone, String userType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.userType = userType;
        this.isActive = true;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public boolean isActive() {


        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

// Getters and Setters omitted for brevity
}