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
    private final TransferFetcherService transferFetcherService;
    private final SummaryGeneratorService summaryGeneratorService;
    private final NotificationService notificationService;

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

    public List<TransferRumorDTO> getRumorsForUser(Long userId) {
        List<UserPreference> preferences = preferenceRepo.findByUserId(userId);
        List<TransferRumorDTO> rumors = new ArrayList<>();

        for (UserPreference pref : preferences) {
            rumors.addAll(transferNewsService.fetchTransferRumors(pref.getClubIdApi()));
        }

        return rumors;
    }
}
