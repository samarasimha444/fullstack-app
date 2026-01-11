package com.example.backend.controller;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import com.example.backend.entity.Message;
import com.example.backend.service.MessageService;


@Controller
public class MessageController {

    private final MessageService messageService;
     public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }



    // app/chat.send
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Message message, Principal principal) {
        messageService.sendMessage(message, principal);
    }

}
