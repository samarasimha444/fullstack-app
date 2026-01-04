package com.example.backend.service;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import com.example.backend.entity.Message;
import com.example.backend.entity.User;
import com.example.backend.repository.MessageRepository;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;
    private final MessageRepository MessageRepo;

    public UserService(UserRepository repo ,MessageRepository MessageRepo) {
        this.repo = repo;
        this.MessageRepo=MessageRepo;
    }



    //current user data
   public User me() {
     Authentication auth =  org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
       String id=auth.getName();
       return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
   }



   //receiver
     public List<User> findReceiversByEmail(String email) {
         if (email == null || email.trim().length() < 2) {
            return List.of();
        }
          return repo.findTop10ByEmailContainingIgnoreCase(email.trim());
    }



   //chat history
    public List<Message> getChatHistory(String myId, String receiverId) {
        return MessageRepo.findChatHistory(myId, receiverId);
    }



    
}
