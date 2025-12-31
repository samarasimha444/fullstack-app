package com.example.backend.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;




@RestController
public class Controller {


   @GetMapping("/")
public String hello(Authentication authentication) {

    if (authentication == null) {
        return "Hello, Guest!";
    }

    // principal = Google sub (String)
    String userId = (String) authentication.getPrincipal();

    return "Hello, user with id: " + userId;
}


}
