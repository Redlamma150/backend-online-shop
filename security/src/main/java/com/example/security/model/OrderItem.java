package com.example.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItem {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("item_id")
    private Long itemId;

    private String title;
    @JsonProperty("image_url")
    private String imageUrl;

    private int quantity;

    @JsonProperty("unit_price")
    private double unitPrice;

    public OrderItem() {
    }

    public OrderItem(Long id, Long orderId, Long itemId, String title, String imageUrl, int quantity, double unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
