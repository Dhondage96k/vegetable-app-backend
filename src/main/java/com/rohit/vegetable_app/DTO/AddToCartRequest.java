package com.rohit.vegetable_app.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AddToCartRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String productId;

    @Min(1)
    private int quantity;

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
// getters & setters
}