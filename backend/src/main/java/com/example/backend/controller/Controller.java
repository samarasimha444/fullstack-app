package com.example.backend.controller;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;;





@RestController
public class Controller {
     
    private UserService service;
    public Controller(UserService service) {
        this.service = service;
    }


    //me endpoint
    @GetMapping("/me")
    public User me() {
        return service.me();
    }

    //receiver
    @GetMapping("/receiver")
public List<User> getReceivers(@RequestParam String q) {
    return service.findReceiversByEmail(q);
    }

    


 }
