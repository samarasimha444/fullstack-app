package com.example.backend.controller;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.entity.Message;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;
import com.example.backend.repository.MessageRepository;





@RestController
public class Controller {
     
    
    private StringRedisTemplate redis;
    private UserService service;
    private MessageRepository MessageRepo;
   
    public Controller(UserService service ,MessageRepository messageRepo) {
        this.service = service;
        this.MessageRepo=messageRepo;
    }


    //me endpoint
  @GetMapping("/me")
    public User me() throws Exception {
   return service.me();
    }


    
    @GetMapping("/testing")
    public Long login(){
        return redis.opsForValue().increment("count");
    }
    




    //reciever
    @GetMapping("/receiver")
public List<User> getReceivers(@RequestParam String q) {
    return service.findReceiversByEmail(q);
    }


    
    //chat history
    @GetMapping("/history/{receiverId}")
     public List<Message> getChatHistory(
            @PathVariable String receiverId,
            Principal principal
    ) {
        // principal.getName() = logged-in userId (google sub)
        String myId = principal.getName();

        return service.getChatHistory(myId, receiverId);
    }


 }
