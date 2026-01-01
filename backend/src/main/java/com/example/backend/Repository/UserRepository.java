package com.example.backend.repository;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import  java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    //find by email
     List<User> findTop10ByEmailContainingIgnoreCase(String email);

}
