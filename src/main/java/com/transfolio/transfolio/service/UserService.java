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
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return null;
        }
        return userRepo.save(user);
    }

    // 🔁 Username-based login
    public Object login(String username, String password) {
        Optional<User> userOpt = userRepo.findByUsername(username);

        if (userOpt.isEmpty()) {
            return 0; // user not found
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(password)) {
            return 1; // invalid password
        }

        return user; // success
    }

    public Optional<User> getById(Long id) {
        return userRepo.findById(id);
    }
}
