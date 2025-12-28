package com.example.backend;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "providerId"}), // One user per OAuth provider
        @UniqueConstraint(columnNames = {"email"})                   // Email must be unique
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                     // Primary key (internal user ID)

    @Column(nullable = false)
    private String email;                // User email (verified by provider)

    private String name;                 // Display name from provider

    private String picture;              // Profile picture URL (Google CDN)

    @Column(nullable = false)
    private String provider;             // Auth provider (GOOGLE, LOCAL, etc.)

    @Column(nullable = false)
    private String providerId;            // Provider-specific user ID (Google "sub")

    private LocalDateTime createdAt;      // Account creation timestamp
}
