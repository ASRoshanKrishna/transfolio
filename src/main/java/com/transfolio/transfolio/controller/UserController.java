package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.model.User;
import com.transfolio.transfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.register(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Optional<User> loggedIn = userService.login(user.getEmail(), user.getPassword());
        return loggedIn.isPresent()
                ? ResponseEntity.ok("✅ Login successful. Welcome " + loggedIn.get().getUsername())
                : ResponseEntity.status(401).body("❌ Invalid credentials");
    }
}
