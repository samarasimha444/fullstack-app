package com.example.backend;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component // Register this filter as a Spring bean
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // Utility for JWT validation

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = null; // Will hold JWT from cookie

        // Extract JWT from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("AUTH_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue(); // JWT found
                    break;
                }
            }
        }

        // If token exists, validate it
        if (token != null) {
            try {
                Claims claims = jwtUtil.validateToken(token); // Verify JWT

                // Create authenticated user context
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),  // User ID from JWT
                                null,                 // No credentials needed
                                Collections.emptyList() // No roles for now
                        );

                // Store authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // Invalid or expired JWT
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Stop request processing
            }
        }

        // Continue with next filter / controller
        filterChain.doFilter(request, response);
    }
}
