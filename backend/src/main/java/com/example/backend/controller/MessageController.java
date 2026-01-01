package com.example.backend.controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import com.example.backend.entity.Message;
import com.example.backend.repository.MessageRepository;

@Controller
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository MessageRepo;

    public MessageController(SimpMessagingTemplate messagingTemplate,MessageRepository MessageRepo) {
        this.MessageRepo=MessageRepo;
        this.messagingTemplate = messagingTemplate;
    }


     //app/chat.send
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload Message message,
            Principal principal) 
    {

        // ✅ JWT principal (STRING userId)
        String senderId = principal.getName();

        // 🔒 NEVER trust client-sent senderId
        message.setSenderId(senderId);

        // Deliver message privately to receiver
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),   // userId
                "/queue/messages",
                message
        );

        MessageRepo.save(message);
       
    }



    // 🔹 GET /api/messages/{receiverId}
   
}
