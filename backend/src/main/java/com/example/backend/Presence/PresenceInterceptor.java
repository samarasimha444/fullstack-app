package com.example.backend.Presence;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class PresenceInterceptor implements ChannelInterceptor {

    private final OnlineUserStore onlineUserStore;
    private final SimpMessagingTemplate messagingTemplate;

    public PresenceInterceptor(
            OnlineUserStore onlineUserStore,
            @Lazy SimpMessagingTemplate messagingTemplate
    ) {
        this.onlineUserStore = onlineUserStore;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == null) return message;

        String username =
                accessor.getUser() != null
                        ? accessor.getUser().getName()
                        : null;

        // 🔹 CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand()) && username != null) {

            onlineUserStore.markOnline(username);

            // 1️⃣ SEND EXISTING ONLINE USERS TO NEW USER
            for (String user : onlineUserStore.getAllOnlineUsers()) {
                messagingTemplate.convertAndSendToUser(
                        username,
                        "/queue/presence-init",
                        new PresenceEvent(user, "ONLINE")
                );
            }

            // 2️⃣ NOTIFY EVERYONE ELSE
            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    new PresenceEvent(username, "ONLINE")
            );
        }

        // 🔹 DISCONNECT
        if (StompCommand.DISCONNECT.equals(accessor.getCommand()) && username != null) {

            onlineUserStore.markOffline(username);

            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    new PresenceEvent(username, "OFFLINE")
            );
        }

        return message;
    }
}
