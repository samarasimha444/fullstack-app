package com.example.backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.Entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Fetch full chat history between two users
     * (sender -> receiver OR receiver -> sender)
     * Ordered by timestamp ascending (old → new)
     */
    List<Message> findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(
            String sender1,
            String receiver1,
            String sender2,
            String receiver2
    );

     // All messages involving user, latest first
    List<Message> findBySenderOrReceiverOrderByTimestampDesc(
            String sender,
            String receiver
    );
}
