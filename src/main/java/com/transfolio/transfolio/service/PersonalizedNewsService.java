package com.transfolio.transfolio.service;

import com.transfolio.transfolio.model.NewsEntry;
import com.transfolio.transfolio.model.RumorEntry;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.repository.NewsEntryRepository;
import com.transfolio.transfolio.repository.UserPreferenceRepository;
import com.transfolio.transfolio.repository.RumorEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalizedNewsService {

    private final UserPreferenceRepository preferenceRepo;
    private final NewsEntryRepository newsRepo;
    private final RumorEntryRepository rumorRepo;
    private final TransferNewsService transferNewsService;
    private final TransferFetcherService transferFetcherService;

    public List<NewsEntry> getNewsForUser(Long userId) {
        List<UserPreference> preferences = preferenceRepo.findByUserId(userId);

        // Step 1: Ensure latest data is stored
        for (UserPreference pref : preferences) {
            transferFetcherService.fetchAndStoreTransfers(pref);
        }

        // Step 2: Fetch ALL transfer news from DB for the user's tracked clubs
        List<NewsEntry> finalResult = new ArrayList<>();
        for (UserPreference pref : preferences) {
            finalResult.addAll(newsRepo.findByClub_IdOrderByTransferDateDesc(pref.getClubIdApi()));
        }

        return finalResult;
    }

    public List<RumorEntry> getRumorsForUser(Long userId) {
        List<UserPreference> preferences = preferenceRepo.findByUserId(userId);

        List<RumorEntry> allRumors = new ArrayList<>();

        for (UserPreference pref : preferences) {
            // 1. Fetch + Store fresh rumors into DB (if new)
            transferNewsService.fetchAndStoreRumors(pref.getClubIdApi(), pref.getCompetitionId(), pref.getUser());

            // 2. Add all from DB to result
            allRumors.addAll(rumorRepo.findByTrackedClubIdOrderByLastPostDateDesc(pref.getClubIdApi()));
        }

        return allRumors;
    }
}
