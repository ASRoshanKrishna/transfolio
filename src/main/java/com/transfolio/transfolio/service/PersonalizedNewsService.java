package com.transfolio.transfolio.service;

import com.transfolio.transfolio.service.NotificationService;
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
        List<NewsEntry> finalResult = new ArrayList<>();

        for (UserPreference pref : preferences) {
            // 💡 Step 1: Fetch live data from API
            List<NewsEntry> liveTransfers = transferFetcherService.fetchAndStoreTransfers(pref);

            for (NewsEntry transfer : liveTransfers) {
                // 💡 Step 2: Check if already exists in DB (e.g. by player + club + date)
                boolean exists = newsRepo.existsByPlayer_IdAndTransferDateAndClub_Id(
                        transfer.getPlayer().getId(),
                        transfer.getTransferDate(),
                        transfer.getClub().getId()
                );

                if (!exists) {
                    // 💡 Step 3: Save transfer
                    newsRepo.save(transfer);

                    // 💡 Step 4: Generate Summary
                    String summary = summaryGeneratorService.generateSummary(transfer);
                    transfer.setSummary(summary);

                    // 💡 Step 5: Notify the user
                    notificationService.notifyUser(pref.getUser().getId(), summary);

                    try {
                        Thread.sleep(1000); // Gemini rate-limiting
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Save updated summary
                    newsRepo.save(transfer);
                }

                finalResult.add(transfer);
            }
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
