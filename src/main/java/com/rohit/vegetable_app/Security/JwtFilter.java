package com.rohit.vegetable_app.Security;

import com.rohit.vegetable_app.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends GenericFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                String email = jwtUtil.extractEmail(token);

                if (!jwtUtil.isExpired(token)) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(email, null, List.of());

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // ✅ Important for your controllers
                    req.setAttribute("email", email);
                }

            } catch (Exception e) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("Invalid or expired token");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}