package com.example.backend.Presence;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUserStore {

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public void markOnline(String username) {
        onlineUsers.add(username);
    }

    public void markOffline(String username) {
        onlineUsers.remove(username);
    }

    public Set<String> getAllOnlineUsers() {
        return onlineUsers;
    }
}
