package com.example.security.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {
    private Long id;

    private String title;

    @JsonProperty("image_url")
    private String imageUrl;

    private Double price;
    private int stock;

    public Item() {
    }

    public Item(Long id, String title, String imageUrl, Double price, int stock) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}

