package com.example.backend.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.entity.Message;
import java.util.List;



@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

    @Query("""
        SELECT m FROM Message m
        WHERE 
          (m.senderId = :user1 AND m.receiverId = :user2)
          OR
          (m.senderId = :user2 AND m.receiverId = :user1)
        ORDER BY m.timestamp ASC
    """)
    List<Message> findChatHistory(
            @Param("user1") String user1,
            @Param("user2") String user2
    );

}