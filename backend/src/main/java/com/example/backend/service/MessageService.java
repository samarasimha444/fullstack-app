package com.example.backend.service;

import java.security.Principal;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.Message;
import com.example.backend.repository.MessageRepository;

@Service
public class MessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepo;

    public MessageService(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepo) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepo = messageRepo;
    }

    @Transactional
    public void sendMessage(Message message, Principal principal) {
        // ✅ JWT principal (STRING userId)
        String senderId = principal.getName();

        // 🔒 Never trust client-sent senderId
        message.setSenderId(senderId);

        // ✅ deliver message to receiver privately
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),    // receiver userId
                "/queue/messages",
                message
        );

        // ✅ persist message
        messageRepo.save(message);
    }
}
