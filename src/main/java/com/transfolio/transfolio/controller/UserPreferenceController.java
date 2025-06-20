package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.dto.UserPreferenceDTO;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/preferences")
public class UserPreferenceController {

    @Autowired
    private UserPreferenceService service;

    @PostMapping
    public UserPreference savePreference(@RequestBody UserPreferenceDTO dto) {
        return service.savePreference(dto);
    }

    @GetMapping("/{userId}")
    public List<UserPreference> getPreferences(@PathVariable Long userId) {
        return service.getPreferencesForUser(userId);
    }
}
