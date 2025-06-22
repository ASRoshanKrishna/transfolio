package com.transfolio.transfolio.service;

import com.transfolio.transfolio.dto.UserPreferenceDTO;
import com.transfolio.transfolio.model.User;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.repository.UserPreferenceRepository;
import com.transfolio.transfolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferenceService {

    @Autowired
    private UserPreferenceRepository repository;
    @Autowired
    private UserRepository userRepo; // Make sure this is present

    public UserPreference savePreference(UserPreferenceDTO dto) {
        // Step 1: Check if already exists
        boolean exists = repository.existsByUser_IdAndClubIdApi(dto.getUserId(), dto.getClubIdApi());
        if (exists) {
            throw new RuntimeException("Preference already exists");
        }

        // Step 2: If not, continue as before
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

        UserPreference pref = new UserPreference();
        pref.setUser(user);
        pref.setClubIdApi(dto.getClubIdApi());
        pref.setClubName(dto.getClubName());
        pref.setCompetitionId(dto.getCompetitionId());
        pref.setCompetitionName(dto.getCompetitionName());
        pref.setLogoUrl(dto.getLogoUrl());

        return repository.save(pref);
    }

    public List<UserPreference> getPreferencesForUser(Long userId) {
        return repository.findByUserId(userId);
    }
}
