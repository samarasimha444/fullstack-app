package com.example.backend.Controller;
import com.example.backend.DTO.ChatMessage;
import com.example.backend.Entity.Message;
import com.example.backend.Entity.MessageStatus;
import com.example.backend.Repository.MessageRepository;
import com.example.backend.Repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ChatWebSocketController(
            SimpMessagingTemplate messagingTemplate,
            UserRepository userRepository,
            MessageRepository messageRepository
    ) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage chatMessage, Principal principal) {

        // 1️⃣ Sender identity from WebSocket Principal
        String senderEmail = principal.getName();

        Long senderId = userRepository.findIdByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        // 2️⃣ Receiver identity
        Long receiverId = userRepository.findIdByEmail(chatMessage.getReceiverEmail())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // 3️⃣ Persist message
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(chatMessage.getContent())
                .sentAt(Instant.now())
                .status(MessageStatus.SENT)
                .build();

        messageRepository.save(message);

        // 4️⃣ Send message to receiver in real time
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverEmail(),
                "/queue/messages",
                chatMessage
        );
    }
}
