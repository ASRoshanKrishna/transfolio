package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.dto.UserLoginDTO;
import com.transfolio.transfolio.model.User;
import com.transfolio.transfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.register(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody UserLoginDTO loginDTO) {
        User user = userService.login(loginDTO.getEmail(), loginDTO.getPassword());
        System.out.println("Welcome " + user.getUsername());
        return ResponseEntity.ok(user);
    }
}
