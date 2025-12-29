package com.example.backend.WebSockets;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.List;

public class CookieUtil {

    public static String extractJwtFromCookie(StompHeaderAccessor accessor) {

        List<String> cookies = accessor.getNativeHeader("cookie");
        if (cookies == null) return null;

        for (String cookie : cookies) {
            for (String c : cookie.split(";")) {
                c = c.trim();
                if (c.startsWith("JWT=")) {
                    return c.substring(4);
                }
            }
        }
        return null;
    }
}
