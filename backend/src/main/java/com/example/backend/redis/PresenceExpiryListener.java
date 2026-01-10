package com.example.backend.redis;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;


@Component
public class PresenceExpiryListener implements MessageListener {

    private static final String PREFIX = "presence:user:";

    private final PresenceBroadcaster broadcaster;

    public PresenceExpiryListener(PresenceBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (!expiredKey.startsWith(PREFIX)) return;

        String userId = expiredKey.substring(PREFIX.length());

        System.out.println("User OFFLINE: " + userId);
        broadcaster.userOffline(userId);
    }
}
