package com.example.backend.redis;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class PresenceWsController {

    private final PresenceService presenceService;
    private final PresenceBroadcaster broadcaster;

    public PresenceWsController(PresenceService presenceService,
                                PresenceBroadcaster broadcaster) {
        this.presenceService = presenceService;
        this.broadcaster = broadcaster;
    }

    @MessageMapping("/heartbeat") // frontend sends to /app/heartbeat
    public void heartbeat(Principal principal) {

        // google sub (you said you use this as principal)
        String userId = principal.getName();

        // refresh ttl in redis
        presenceService.heartbeat(userId);

        // ✅ IMPORTANT: always broadcast ONLINE
        // so UI syncs even if it subscribed late / refreshed / reconnected
        broadcaster.userOnline(userId);
    }
}
