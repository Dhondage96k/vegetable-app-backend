package com.rohit.vegetable_app.service;

import com.rohit.vegetable_app.DTO.LoginRequest;
import com.rohit.vegetable_app.DTO.RegisterRequest;
import com.rohit.vegetable_app.Exception.InvalidCredentialsException;
import com.rohit.vegetable_app.Exception.UserAlreadyExistsException;
import com.rohit.vegetable_app.Model.User;
import com.rohit.vegetable_app.Repository.UserRepository;
import com.rohit.vegetable_app.responce.ApiResponse;
import com.rohit.vegetable_app.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ApiResponse<String> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = new User();
        user.setName(request.getName());          // ✅ NEW
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobile(request.getMobile());      // ✅ NEW
        user.setRole("USER");

        userRepository.save(user);

        return new ApiResponse<>(true, "User registered successfully", user.getEmail(),""+user.getRole());
    }
    public ApiResponse<Object> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // ✅ Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new ApiResponse<>(
                true,
                "Login successful",
                 user.getEmail(),""
        );
    } 
}
