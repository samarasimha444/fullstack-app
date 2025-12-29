package com.example.backend.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.Entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
