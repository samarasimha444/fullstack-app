package com.example.backend.redis;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/presence")
public class HeartbeatController {

    private final PresenceService presenceService;
    private final PresenceBroadcaster broadcaster;

    public HeartbeatController(PresenceService presenceService,
                               PresenceBroadcaster broadcaster) {
        this.presenceService = presenceService;
        this.broadcaster = broadcaster;
    }

    @PostMapping("/heartbeat")
    public Map<String, Object> heartbeat(@RequestParam String userId) {
        boolean firstOnline = presenceService.heartbeat(userId);

        // broadcast only when user comes online first time
        if (firstOnline) {
            broadcaster.userOnline(userId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("status", "ONLINE");
        response.put("firstOnline", firstOnline);
        response.put("ttlSeconds", presenceService.getTtlSeconds(userId));
        return response;
    }

    @GetMapping("/is-online")
    public Map<String, Object> isOnline(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("online", presenceService.isOnline(userId));
        response.put("ttlSeconds", presenceService.getTtlSeconds(userId));
        return response;
    }
}
