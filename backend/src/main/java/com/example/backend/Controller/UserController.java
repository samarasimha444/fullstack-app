package com.example.backend.Controller;
import com.example.backend.Entity.Message;
import com.example.backend.Entity.UserEntity;
import com.example.backend.Service.MessageService;
import com.example.backend.Service.UserService;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController

public class UserController {

    private final UserService userService;
    private final MessageService messageService;

    //login 
    @PostMapping("/login")
    public String login(@RequestBody UserEntity user) {
        return userService.login(user);
    }
    
    //signup
    @PostMapping("/signup")
    public String signup(@RequestBody UserEntity user) {
        return userService.signup(user);
    }

    // search
    @GetMapping("/users/search")
    public List<String> searchUsers(@RequestParam String query) {
        return userService.searchUsernames(query);
    }


    //user list
    @GetMapping("/users")
    public List<String> getUsers() {
        return userService.getAllUsernames();
    }

     // chat history
     @GetMapping("/chat/history")
    public List<Message> getChatHistory(
            @RequestParam String receiver,
            Principal principal
    ) {
        String sender = principal.getName();
        return messageService.getChatHistory(sender, receiver);
    }


    // recents
    @GetMapping("/chat/recents")
    public List<Message> getRecentChats(Principal principal) {
        return messageService.getRecentChats(principal.getName());
    }

}
