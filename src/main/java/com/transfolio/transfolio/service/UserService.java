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

    public Optional<User> login(String email, String password) {
        return userRepo.findByEmail(email)
                .filter(u -> u.getPassword().equals(password)); // plain-text, can upgrade later
    }

    public Optional<User> getById(Long id) {
        return userRepo.findById(id);
    }
}
