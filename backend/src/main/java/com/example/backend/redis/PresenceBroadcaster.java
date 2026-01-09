package com.example.backend.redis;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PresenceBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    public PresenceBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void userOnline(String userId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", String.valueOf(userId));  // ✅ enforce string
        payload.put("status", "ONLINE");

        messagingTemplate.convertAndSend("/topic/presence", payload);
    }

    public void userOffline(String userId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", String.valueOf(userId));  // ✅ enforce string
        payload.put("status", "OFFLINE");

        messagingTemplate.convertAndSend("/topic/presence", payload);
    }
}
