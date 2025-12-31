package com.example.backend.service;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import com.example.backend.entity.User;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }



    //current user data
   public User me() {
     Authentication auth =  org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
       String id=auth.getName();
       return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
   }

   

}
