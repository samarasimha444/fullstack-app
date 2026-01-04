package com.example.backend;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    private IpUtils() {
        // utility class
    }

    public static String getClientIp(HttpServletRequest request) {

        // Case 1: Behind proxy / load balancer
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // X-Forwarded-For can contain multiple IPs: client, proxy1, proxy2
            return forwardedFor.split(",")[0].trim();
        }

        // Case 2: Local development or direct connection
        return request.getRemoteAddr(); // usually 127.0.0.1 or ::1
    }
}
