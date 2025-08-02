package com.example.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Favorite {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("item_id")
    private Long itemId;

    public Favorite() {
    }

    public Favorite(Long id, Long userId, Long itemId) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}

