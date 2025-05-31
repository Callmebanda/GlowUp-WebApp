package com.glowup.model;

public class Service {
    private int serviceId;
    private int salonId;
    private String name;
    private String description;
    private String category;
    private int durationMinutes;
    private double price;
    private String gender;

    // Full constructor
    public Service() {
        this.serviceId = serviceId;
        this.salonId = salonId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.gender = gender;
    }

    // Getters and setters
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public int getSalonId() { return salonId; }
    public void setSalonId(int salonId) { this.salonId = salonId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}