package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.dto.UserLoginDTO;
import com.transfolio.transfolio.dto.UserResponseDTO;
import com.transfolio.transfolio.model.User;
import com.transfolio.transfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.register(user);

        if(savedUser == null) {
            return ResponseEntity.status(409).build(); // 409 Conflict
        }

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")

    public ResponseEntity<UserResponseDTO> login(@RequestBody UserLoginDTO loginDTO) {
        User user = userService.login(loginDTO.getEmail(), loginDTO.getPassword());

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        return ResponseEntity.ok(dto);
    }
}
