package com.rohit.vegetable_app.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class ErrorResponse {
    private String error;
    private String message;
    private int status;
    private long timestamp;

    public ErrorResponse(String internalServerError, String message, int value, long l) {
    }
}