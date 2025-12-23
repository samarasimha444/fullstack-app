package com.example.backend.Presence;

public class PresenceEvent {

    private String username;
    private String status; // ONLINE / OFFLINE

    public PresenceEvent() {}

    public PresenceEvent(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
