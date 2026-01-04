package com.example.backend.service;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    // Rule: 5 requests per 60 seconds
    private static final int LIMIT = 5;
    private static final int WINDOW_SECONDS = 60;

    public boolean isAllowed(String key) {

        Long count = redisTemplate
                .opsForValue()
                .increment(key);

        // First request → start the time window
        if (count != null && count == 1) {
            redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        // Allow only within limit
        return count != null && count <= LIMIT;
    }
}
