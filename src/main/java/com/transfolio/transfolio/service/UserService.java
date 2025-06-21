package com.transfolio.transfolio.service;

import com.transfolio.transfolio.model.User;
import com.transfolio.transfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public User register(User user) {
        // Optionally add validation checks here
        return userRepo.save(user);
    }

    public User login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public Optional<User> getById(Long id) {
        return userRepo.findById(id);
    }
}
