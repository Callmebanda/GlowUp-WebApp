package com.glowup.model;

import java.math.BigDecimal;

public class Salon {
    private int salonId;
    private String name;
    private String description;
    private String address;
    private String city;
    private String phone;
    private String email;
    private int ownerId;
    private String imageUrl;
    private double rating;

    // Constructors, getters, and setters
    public Salon(int salonId, String name, String description, String address, String city, String phone, String email, String imageUrl, BigDecimal rating) {}

    public Salon() {
        this.salonId = salonId;
        this.name = name;
        this.address = address;
    }
    public int getSalonId() {
        return salonId;
    }
    public void setSalonId(int salonId) {
        this.salonId = salonId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}