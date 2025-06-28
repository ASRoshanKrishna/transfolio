package com.transfolio.transfolio.repository;

import com.transfolio.transfolio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);  // ✅ Used in updated login
}
