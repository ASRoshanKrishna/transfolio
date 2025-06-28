package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.dto.UserLoginDTO;
import com.transfolio.transfolio.dto.UserResponseDTO;
import com.transfolio.transfolio.model.User;
import com.transfolio.transfolio.security.JwtUtil;
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
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.register(user);

        if(savedUser == null) {
            return ResponseEntity.status(409).build(); // 409 Conflict
        }

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) {
        Object result = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

        if (result instanceof Integer) {
            int code = (Integer) result;
            if (code == 0) {
                return ResponseEntity.status(404).body("User not found");
            } else if (code == 1) {
                return ResponseEntity.status(401).body("Invalid password");
            }
        }

        assert result instanceof User;
        User user = (User) result;
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        String token = jwtUtil.generateToken(String.valueOf(user.getId()));
        dto.setToken(token); // Add this field to UserResponseDTO if needed

        return ResponseEntity.ok(dto);
    }

}
