package com.example.backend;

import com.example.backend.service.RateLimitService;
import com.example.backend.IpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Apply rate limiting ONLY for login endpoint
        if (request.getRequestURI().equals("/testing")) {

            String ip = IpUtils.getClientIp(request);
            String key = "rate:login:ip:" + ip;

            boolean allowed = rateLimitService.isAllowed(key);

            if (!allowed) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Too many requests. Try again later.");
                return; // STOP request
            }
        }

        filterChain.doFilter(request, response);
    }
}
