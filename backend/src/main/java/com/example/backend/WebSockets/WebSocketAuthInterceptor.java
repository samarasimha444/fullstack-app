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

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

        String token = CookieUtil.extractJwtFromCookie(accessor);

        if (token == null) {
            return null; // ❌ reject CONNECT cleanly
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);

            String email = userRepository.findEmailById(userId)
                    .orElse(null);

            if (email == null) {
                return null;
            }

            accessor.setUser(() -> email);

        } catch (Exception e) {
            return null; // ❌ reject CONNECT cleanly
        }
    }

    return message;
}

}
