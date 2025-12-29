package com.example.backend.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sender user ID
    @Column(nullable = false)
    private Long senderId;

    // Receiver user ID
    @Column(nullable = false)
    private Long receiverId;

    // Message content
    @Column(nullable = false, length = 1000)
    private String content;

    // Message timestamp
    @Column(nullable = false)
    private Instant sentAt;

    // Message status (SENT, DELIVERED, READ)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;
    
}
