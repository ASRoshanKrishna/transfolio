package com.transfolio.transfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfolio.transfolio.model.RumorEntry;
import com.transfolio.transfolio.model.User;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.repository.RumorEntryRepository;
import com.transfolio.transfolio.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Service
public class TransferNewsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final RumorEntryRepository rumorRepo;
    private final SummaryGeneratorService summaryService;
    private final UserPreferenceRepository preferenceRepo;
    private final NotificationService notificationService;
    private final String userApiKey;
    private final String rumorApiKey;
    private final String apiHost;

    public TransferNewsService(
            RumorEntryRepository rumorRepo,
            SummaryGeneratorService summaryService,
            UserPreferenceRepository preferenceRepo,
            NotificationService notificationService,
            @Value("${rapid.api.key.user}") String userApiKey,
            @Value("${rapid.api.key.rumor}") String rumorApiKey,
            @Value("${rapid.api.host}") String apiHost
    ) {
        this.rumorRepo = rumorRepo;
        this.summaryService = summaryService;
        this.preferenceRepo = preferenceRepo;
        this.notificationService = notificationService;
        this.userApiKey = userApiKey;
        this.rumorApiKey = rumorApiKey;
        this.apiHost = apiHost;
    }

    public void fetchRumorsForAllUsers() {
        List<UserPreference> allPreferences = preferenceRepo.findAll();

        for (UserPreference pref : allPreferences) {
            try {
                fetchAndStoreRumors(pref.getClubIdApi(), pref.getCompetitionId(), pref.getUser(), 1); // mode=1 scheduler
                Thread.sleep(1000); // Gemini rate safety
            } catch (Exception e) {
                System.err.println("❌ Failed to fetch/store rumors for clubId=" + pref.getClubIdApi());
                e.printStackTrace();
            }
        }
    }

    public void fetchAndStoreRumors(String clubId, String competitionId, User user, int mode) {
        String apiKeyToUse = (mode == 1) ? rumorApiKey : userApiKey;

        String url = "https://transfermarket.p.rapidapi.com/transfers/list-rumors?clubIds=" + clubId +
                "&competitionIds=" + competitionId + "&sort=date_desc&domain=com";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKeyToUse);
        headers.set("X-RapidAPI-Host", apiHost);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode rumors = root.path("rumors");

                for (JsonNode item : rumors) {
                    String fromId = item.path("fromClubID").asText();
                    String toId = item.path("toClubID").asText();

                    if (!clubId.equals(fromId) && !clubId.equals(toId)) continue;

                    String rumorId = item.path("id").asText();
                    if (rumorRepo.existsByOriginalRumorIdAndUser(rumorId, user)) continue;

                    RumorEntry rumor = RumorEntry.builder()
                            .originalRumorId(rumorId)
                            .playerID(item.path("playerID").asText())
                            .fromClubID(fromId)
                            .toClubID(toId)
                            .isClosed(item.path("isClosed").asBoolean())
                            .probability(item.path("probability").asText())
                            .progression(item.path("progression").asText())
                            .threadUrl(item.path("threadUrl").asText())
                            .lang(item.path("lang").asText())
                            .lastPostDate(item.path("lastPostDate").asLong())
                            .closedType(item.path("closedType").asText())
                            .marketValue(item.path("marketValue").path("value").asLong())
                            .currency(item.path("marketValue").path("currency").asText())
                            .trackedClubId(clubId)
                            .user(user)
                            .build();

                    String context = """
                            Rumor for playerID: %s
                            From Club ID: %s
                            To Club ID: %s
                            Market Value: %s%s
                            Probability: %s
                            Thread: %s
                            Closed: %s
                            """.formatted(
                            rumor.getPlayerID(),
                            rumor.getFromClubID(),
                            rumor.getToClubID(),
                            rumor.getMarketValue(), rumor.getCurrency(),
                            rumor.getProbability(),
                            rumor.getThreadUrl(),
                            rumor.isClosed() ? "Yes" : "No"
                    );

                    new Thread(() -> {
                        try {
                            String summary = summaryService.generateRumorSummary(context);
                            rumor.setSummary(summary);
                            rumorRepo.save(rumor);
                            notificationService.notifyUser(user.getId(), summary);
                            Thread.sleep(1000); // optional
                        } catch (Exception ex) {
                            System.err.println("⚠️ Error in rumor summary thread: " + ex.getMessage());
                        }
                    }).start();
                }

            } catch (Exception e) {
                System.err.println("❌ Error parsing or saving rumors");
                e.printStackTrace();
            }
        } else {
            System.err.println("❌ Failed to fetch rumors for clubId = " + clubId);
        }
    }

    public List<RumorEntry> getRumorsByClub(String clubId) {
        return rumorRepo.findByTrackedClubIdOrderByLastPostDateDesc(clubId);
    }
}