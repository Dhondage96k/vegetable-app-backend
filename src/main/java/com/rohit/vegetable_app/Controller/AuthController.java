package com.rohit.vegetable_app.Controller;

import com.rohit.vegetable_app.DTO.LoginRequest;
import com.rohit.vegetable_app.DTO.RegisterRequest;
import com.rohit.vegetable_app.Model.User;
import com.rohit.vegetable_app.responce.ApiResponse;
import com.rohit.vegetable_app.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}