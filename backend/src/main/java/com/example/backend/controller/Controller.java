package com.example.backend.controller;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;




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


    




}
