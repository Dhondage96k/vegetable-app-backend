package com.rohit.vegetable_app.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ✅ Runtime Exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {

        logger.error("Runtime Exception: ", ex);

        ErrorResponse error = new ErrorResponse(
                "Runtime Error",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ✅ Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        logger.error("Unhandled Exception: ", ex);

        ErrorResponse error = new ErrorResponse(
                "Internal Server Error",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}