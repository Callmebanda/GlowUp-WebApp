package com.glowup.model;

import java.sql.Time;
import java.sql.Timestamp;

public class Appointment {
    private int appointmentId;
    private int userId;
    private int salonId;
    private int serviceId;
    private String appointmentDate;  // Date part (YYYY-MM-DD)
    private Time startTime;         // Time part (HH:MM:SS)
    private String notes;
    private String status;
    private String serviceName;     // Joined from services table
    private String salonName;       // Joined from salons table
    private Timestamp createdAt;    // Automatic timestamp from database
    // Add this field and methods to your existing Appointment class
    private String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    // Constructors
    public Appointment() {
        this.status = "pending";
    }

    // Constructor for creating new appointments
    public Appointment(int userId, int salonId, int serviceId, String appointmentDate, Time startTime) {
        this();
        this.userId = userId;
        this.salonId = salonId;
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
    }

    // Constructor for full object creation (e.g., from database)
    public Appointment(int appointmentId, int userId, int salonId, int serviceId,
                       String appointmentDate, Time startTime, String status,
                       String serviceName, String salonName, Timestamp createdAt) {
        this(userId, salonId, serviceId, appointmentDate, startTime);
        this.appointmentId = appointmentId;
        this.status = status;
        this.serviceName = serviceName;
        this.salonName = salonName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSalonId() {
        return salonId;
    }

    public void setSalonId(int salonId) {
        this.salonId = salonId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", userId=" + userId +
                ", salonId=" + salonId +
                ", serviceId=" + serviceId +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", startTime=" + startTime +
                ", status='" + status + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", salonName='" + salonName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }
}