package com.transfolio.transfolio.service;

import com.transfolio.transfolio.dto.UserPreferenceDTO;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferenceService {

    @Autowired
    private UserPreferenceRepository repository;

    public UserPreference savePreference(UserPreferenceDTO dto) {
        UserPreference pref = new UserPreference();
        pref.setUserId(dto.getUserId());
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
