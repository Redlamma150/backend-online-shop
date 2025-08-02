package com.example.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Order {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    private LocalDateTime date;
    private String address;
    @JsonProperty("total_price")
    private Double totalPrice;

    private Status status; // TEMP / CLOSE

    public Order() {
    }

    public Order(Long id, Long userId, LocalDateTime date, String address, Double totalPrice, Status status) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.address = address;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
