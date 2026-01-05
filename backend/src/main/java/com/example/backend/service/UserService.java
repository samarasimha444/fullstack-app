package com.example.backend.service;
import com.example.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.backend.entity.Message;
import com.example.backend.entity.User;
import com.example.backend.repository.MessageRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;

@Service
public class UserService {

    private final UserRepository repo;
    private final MessageRepository MessageRepo;
    private final StringRedisTemplate redis;

    public UserService(UserRepository repo ,MessageRepository MessageRepo,StringRedisTemplate redis) {
        this.repo = repo;
        this.MessageRepo=MessageRepo;
        this.redis=redis;
    }



    //current user data
   public User me() throws Exception {

    Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();

    String id = auth.getName();
    String key = "user:" + id;

    // 1️⃣ Read from Redis
    String json = redis.opsForValue().get(key);

    // 2️⃣ CACHE HIT
    if (json != null) {
        return new ObjectMapper().readValue(json, User.class);
    }

    // 3️⃣ CACHE MISS → DB
    User user = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 4️⃣ Store as JSON
    redis.opsForValue().set(
        key,
        new ObjectMapper().writeValueAsString(user),
        10,
        TimeUnit.MINUTES
    );

    return user;
}




   //receiver
     public List<User> findReceiversByEmail(String email) {
         if (email == null || email.trim().length() < 2) {
            return List.of();
        }
          return repo.findTop10ByEmailContainingIgnoreCase(email.trim());
    }



   //chat history
    public List<Message> getChatHistory(String myId, String receiverId) {
        return MessageRepo.findChatHistory(myId, receiverId);
    }



    
}
