package com.example.backend.WebSockets;

import com.example.backend.Security.JWT.JwtUtil;
import com.example.backend.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil,
                                    UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            String token = CookieUtil.extractJwtFromCookie(accessor);
            if (token == null) {
                throw new IllegalArgumentException("JWT missing");
            }

            try {
                // 1️⃣ Validate token + extract userId
                Long userId = jwtUtil.getUserIdFromToken(token);

                // 2️⃣ Fetch email from DB
                String email = userRepository.findEmailByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // 3️⃣ Set WebSocket Principal
                accessor.setUser(() -> email);

            } catch (Exception e) {
                throw new IllegalArgumentException("Unauthorized WebSocket connection");
            }
        }

        return message;
    }
}
