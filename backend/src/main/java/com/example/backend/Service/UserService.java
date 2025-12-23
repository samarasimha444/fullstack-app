package com.example.backend.Service;

import com.example.backend.Entity.UserEntity;
import com.example.backend.Repository.UserRepository;
import com.example.backend.Security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repo, JwtUtil jwtUtil, BCryptPasswordEncoder encoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }
    

//signup
    public String signup(UserEntity user) {
        if (repo.findByUsername(user.getUsername()).isPresent())
            return "Username already taken";

        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return jwtUtil.generateToken(user.getUsername());
    }
    

//login
      public String login(UserEntity user) {
        return repo.findByUsername(user.getUsername())
                .map(u -> encoder.matches(user.getPassword(), u.getPassword())
                        ? jwtUtil.generateToken(u.getUsername())
                        : "Invalid password")
                .orElse("No user exists");
    }

    //users
   //give list of users from search bar
   public List<String> searchUsernames(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of(); // no garbage results
        }
        return repo.findUsernamesByKeyword(keyword);
    }

    

     public List<String> getAllUsernames() {
        return repo.findAllUsernames();
    }
}


  



