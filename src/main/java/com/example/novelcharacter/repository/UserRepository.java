package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUserName(String username);
    public User findByEmail(String email);
    public User findByUuid(long uuid);
    public User findByUserId(String userId);
}
