package com.example.backend.WebSockets;

import com.example.backend.Security.JWT.JwtUtil;
import com.example.backend.Repository.UserRepository;
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

        // Authenticate only during WebSocket CONNECT
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            // Extract JWT from cookie
            String token = CookieUtil.extractJwtFromCookie(accessor);
            if (token == null) {
                throw new IllegalArgumentException("JWT missing");
            }

            try {
                // Validate token and get user ID
                Long userId = jwtUtil.getUserIdFromToken(token);

                // Fetch user email from DB
                String email = userRepository.findEmailById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Bind user identity to WebSocket session
                accessor.setUser(() -> email);

            } catch (Exception e) {
                throw new IllegalArgumentException("Unauthorized WebSocket connection");
            }
        }

        return message;
    }
}
