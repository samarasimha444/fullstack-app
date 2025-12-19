package com.example.backend.Controller;


import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.backend.Entity.Message;
import com.example.backend.Repository.MessageRepository;

@Controller
public class ChatSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    public ChatSocketController(SimpMessagingTemplate messagingTemplate,
                                MessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
    }

    /**
     * Client sends to: /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void sendMessage(Message message, Principal principal) {

        // 1️⃣ HARD RULE: sender comes from JWT, not client
        message.setSender(principal.getName());

        // 2️⃣ Timestamp is set by @PrePersist in entity
        // (do NOT set it manually here)

        // 3️⃣ Persist message
        Message savedMessage = messageRepository.save(message);

        // 4️⃣ Send ONLY to receiver (private chat)
        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiver(),
                "/queue/messages",
                savedMessage
        );
    }

     



}
