package com.rohit.vegetable_app.config;

import com.rohit.vegetable_app.Security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // ✅ Inject JwtFilter
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ ADMIN ONLY
                        .requestMatchers("/api/products/**").hasRole("ADMIN")
                        .requestMatchers("/api/orders/status").hasRole("ADMIN")

                        // ✅ USER
                        .requestMatchers("/api/cart/**").hasRole("USER")
                        .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )
                // ✅ Add JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}