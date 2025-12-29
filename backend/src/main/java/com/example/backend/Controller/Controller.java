package com.example.backend.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import java.util.Map;

import com.example.backend.Entity.User;
import com.example.backend.Repository.UserRepository;



@RestController
public class Controller {
    private final UserRepository userRepository;

    public Controller(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
  
@GetMapping("/data")
public Map<String, Object> getUserData(Authentication auth) {

    // principal is ALWAYS String
    String subject = (String) auth.getPrincipal();

    // Convert explicitly
    Long userId = Long.valueOf(subject);

   User user = userRepository.findById(userId)
    .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED, "User not found"
    ));
  
    return Map.of(
    "userId", user.getId(),
    "email", user.getEmail(),
    "name", user.getName(),
    "picture", user.getPicture()

);}}
