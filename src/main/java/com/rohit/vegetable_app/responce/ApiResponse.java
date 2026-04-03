package com.rohit.vegetable_app.responce;

import java.util.Map;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String role;

    // ✅ Constructor
    public ApiResponse(boolean success, String message, T data, String role) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.role = role;
    }

    public ApiResponse(boolean success, String loginSuccessful, Map<String, String> token) {
    }


    // ✅ GETTERS (VERY IMPORTANT)
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}