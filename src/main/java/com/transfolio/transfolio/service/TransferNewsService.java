package com.transfolio.transfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfolio.transfolio.model.RumorEntry;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.repository.RumorEntryRepository;
import com.transfolio.transfolio.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferNewsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final RumorEntryRepository rumorRepo;
    private final SummaryGeneratorService summaryService;
    private final UserPreferenceRepository preferenceRepo;

    private final String apiKey = "d50f7c3db6msh432bcd5aaf9319fp1023c8jsn4d74f1def565";
    private final String apiHost = "transfermarket.p.rapidapi.com";
    public void fetchRumorsForAllUsers() {
        List<UserPreference> allPreferences = preferenceRepo.findAll();

        for (UserPreference pref : allPreferences) {
            try {
                fetchAndStoreRumors(pref.getClubIdApi(), pref.getCompetitionId());
                Thread.sleep(1000); // Optional: spacing for Gemini API limits
            } catch (Exception e) {
                System.err.println("❌ Failed to fetch/store rumors for clubId=" + pref.getClubIdApi());
                e.printStackTrace();
            }
        }
    }

    public void fetchAndStoreRumors(String clubId, String competitionId) {
        String url = "https://transfermarket.p.rapidapi.com/transfers/list-rumors?clubIds=" + clubId +
                "&competitionIds=" + competitionId + "&sort=date_desc&domain=com";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
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
                    if (rumorRepo.existsById(rumorId)) continue;

                    RumorEntry rumor = RumorEntry.builder()
                            .id(rumorId)
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
                            .build();

                    // Generate Gemini AI summary
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

                    String summary = summaryService.generateRumorSummary(context);
                    rumor.setSummary(summary);

                    rumorRepo.save(rumor);
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
