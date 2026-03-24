package com.rohit.vegetable_app.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelOrderRequest {
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
}