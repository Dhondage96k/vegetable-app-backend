package com.rohit.vegetable_app.DTO;

import jakarta.validation.constraints.Min;

public class UpdateCartRequest {

    private String userId;
    private String productId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Min(0) // allow 0 (remove item)
    private int quantity;
}