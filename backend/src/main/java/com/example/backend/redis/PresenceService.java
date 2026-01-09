package com.example.backend.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PresenceService {

    private static final int TTL_SECONDS = 15;

    private final StringRedisTemplate redisTemplate;

    public PresenceService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String userId) {
        return "presence:user:" + userId;
    }

    public boolean heartbeat(String userId) {
        String k = key(userId);

        Boolean firstOnline = redisTemplate.opsForValue().setIfAbsent(k, "online");
        redisTemplate.expire(k, TTL_SECONDS, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(firstOnline);
    }

    public boolean isOnline(String userId) {
        Boolean exists = redisTemplate.hasKey(key(userId));
        return Boolean.TRUE.equals(exists);
    }

    public void setOfflineNow(String userId) {
        redisTemplate.delete(key(userId));
    }

    public long getTtlSeconds(String userId) {
        Long ttl = redisTemplate.getExpire(key(userId), TimeUnit.SECONDS);
        return ttl == null ? -2 : ttl;
    }
}
