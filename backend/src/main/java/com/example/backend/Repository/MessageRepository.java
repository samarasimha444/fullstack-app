package com.example.backend.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.entity.Message;



@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

}