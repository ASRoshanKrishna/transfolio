package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.dto.UserPreferenceDTO;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/preferences")
public class UserPreferenceController {

    @Autowired
    private UserPreferenceService service;

    @PostMapping
    public ResponseEntity<?> savePreference(@RequestBody UserPreferenceDTO dto) {
        try {
            UserPreference saved = service.savePreference(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Preference already exists")) {
                return ResponseEntity.status(409).body("Preference already exists");
            }
            return ResponseEntity.status(500).body("Something went wrong: " + e.getMessage());
        }
    }


    @GetMapping("/{userId}")
    public List<UserPreference> getPreferences(@PathVariable Long userId) {
        return service.getPreferencesForUser(userId);
    }
}
