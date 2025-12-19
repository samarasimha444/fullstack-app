package com.example.backend.Controller;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.Entity.UserEntity;
import com.example.backend.Repository.UserRepository;
import com.example.backend.Service.UserService;

import org.apache.catalina.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;




@RestController
public class UserController {

   
    private final UserService service;
    
    public UserController( UserService service) {
        this.service = service;
    }
    

    //login
    @PostMapping("/login")
    public String login(@RequestBody UserEntity user) {
        return service.login(user);
    }

    //signup
    @PostMapping("/signup")
    public String signup(@RequestBody UserEntity user) {
        return service.signup(user);
    }

   
    // GET /users/search?query=sa
    @GetMapping("/users/search")
    public List<String> searchUsers(@RequestParam String query) {
        return service.searchUsernames(query);
    }
}



    
   

    
    
    
    
