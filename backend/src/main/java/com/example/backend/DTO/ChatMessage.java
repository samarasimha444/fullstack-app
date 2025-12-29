package com.example.backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String receiverEmail;
    private String content;
}
