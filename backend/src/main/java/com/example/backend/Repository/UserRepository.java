package com.example.backend.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.Entity.User;
import java.util.List;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    //find email by userId
    Optional<String> findEmailById(Long userId);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    //find user id by email
    Optional<Long> findIdByEmail(String email);

    List<User> findByEmailContainingIgnoreCaseAndEmailNot(
    String keyword,
    String email
);

}
