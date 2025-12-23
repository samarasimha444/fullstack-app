package com.example.backend.Repository;

import com.example.backend.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

     // 🔥 Only fetch usernames (NOT full User objects)
    @Query("""
        select u.username
        from UserEntity u
        where lower(u.username) like lower(concat('%', :keyword, '%'))
    """)
    List<String> findUsernamesByKeyword(@Param("keyword") String keyword);

    // Fetch all usernames
    @Query("select u.username from UserEntity u")
    List<String> findAllUsernames();

}
