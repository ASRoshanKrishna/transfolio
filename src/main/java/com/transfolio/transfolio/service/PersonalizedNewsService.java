package com.transfolio.transfolio.service;

import com.transfolio.transfolio.dto.TransferRumorDTO;
import com.transfolio.transfolio.model.NewsEntry;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.repository.NewsEntryRepository;
import com.transfolio.transfolio.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalizedNewsService {

    private final UserPreferenceRepository preferenceRepo;
    private final NewsEntryRepository newsRepo;
    private final TransferNewsService transferNewsService;

    public List<NewsEntry> getNewsForUser(Long userId) {
        List<UserPreference> preferences = preferenceRepo.findByUserId(userId);
        List<NewsEntry> result = new ArrayList<>();

        for (UserPreference pref : preferences) {
            result.addAll(newsRepo.findByClub_Id(pref.getClubIdApi()));
        }

        return result;
    }

    public List<TransferRumorDTO> getRumorsForUser(Long userId) {
        List<UserPreference> preferences = preferenceRepo.findByUserId(userId);
        List<TransferRumorDTO> rumors = new ArrayList<>();

        for (UserPreference pref : preferences) {
            rumors.addAll(transferNewsService.fetchTransferRumors(pref.getClubIdApi()));
        }

        return rumors;
    }
}
