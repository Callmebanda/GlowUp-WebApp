package com.glowup.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
    private int paymentId;
    private int appointmentId;
    private BigDecimal amount;
    private String paymentMethod; // "credit_card", "mobile_money", etc.
    private String transactionId;
    private String status; // "pending", "completed", "failed"
    private Timestamp paymentDate;

    // Constructors
    public Payment() {}

    public Payment(int appointmentId, BigDecimal amount, String paymentMethod) {
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = "pending";
    }
    public Payment(int appointmentId, BigDecimal amount, String paymentMethod, String transactionId) {
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = "pending";

    }
    public Payment(int appointmentId, BigDecimal amount, String paymentMethod, String transactionId, String status) {
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;

    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
}
public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
}
public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
}
public void setStatus(String status) {
        this.status = status;
}
public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
}
public int getPaymentId() {
        return paymentId;
}
public int getAppointmentId() {
        return appointmentId;
}
public BigDecimal getAmount() {
        return amount;
}
public String getPaymentMethod() {
        return paymentMethod;
}
public String getTransactionId() {
        return transactionId;
}
public String getStatus() {
        return status;
}
public Timestamp getPaymentDate() {
        return paymentDate;
}

}