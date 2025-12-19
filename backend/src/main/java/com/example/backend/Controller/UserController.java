package com.example.backend.Controller;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.Entity.UserEntity;
import com.example.backend.Service.UserService;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.backend.Service.MessageService;
import com.example.backend.Entity.Message;


import java.security.Principal;
import java.util.List;




@RestController
public class UserController {

   
    private final UserService service;
    private final MessageService messageService;
    
    public UserController( UserService service, MessageService messageService) {
        this.service = service;
        this.messageService = messageService;
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


      @GetMapping("chat/history")
    public List<Message> getChatHistory(
            @RequestParam String receiver,
            Principal principal
    ) {

        String sender = principal.getName(); // extracted from JWT

        return messageService.getChatHistory(sender, receiver);
    }

    @GetMapping("chat/recents")
    public List<Message> getRecentChats(Principal principal) {

        String currentUser = principal.getName();

        return messageService.getRecentChats(currentUser);
    }
}




    
   

    
    
    
    
