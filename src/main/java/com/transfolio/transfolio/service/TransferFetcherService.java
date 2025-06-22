package com.transfolio.transfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfolio.transfolio.model.Club;
import com.transfolio.transfolio.model.NewsEntry;
import com.transfolio.transfolio.model.Player;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.repository.ClubRepository;
import com.transfolio.transfolio.repository.NewsEntryRepository;
import com.transfolio.transfolio.repository.PlayerRepository;
import com.transfolio.transfolio.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransferFetcherService {

    private final UserPreferenceRepository preferenceRepo;
    private final NewsEntryRepository newsRepo;
    private final ClubRepository clubRepo;
    private final PlayerRepository playerRepo;
    private final SummaryGeneratorService summaryGeneratorService;
    private final NotificationService notificationService;
    private final String API_URL = "https://transfermarket.p.rapidapi.com/transfers/list-by-club";
    private final String API_KEY = "d50f7c3db6msh432bcd5aaf9319fp1023c8jsn4d74f1def565";
    private final String API_HOST = "transfermarket.p.rapidapi.com";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // 📍 Used by scheduler
    public void fetchTransfersForAllUsers() {
        List<UserPreference> preferences = preferenceRepo.findAll();
        for (UserPreference pref : preferences) {
            fetchAndStoreTransfers(pref);
        }
    }

    // 📍 Used for personalized /news/{userId} live fetch
    public List<NewsEntry> fetchAndStoreTransfers(UserPreference pref) {
        List<NewsEntry> newEntries = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", API_KEY);
        headers.set("X-RapidAPI-Host", API_HOST);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = API_URL + "?id=" + pref.getClubIdApi()
                + "&seasonID=2025&domain=com";

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode arrivals = root.path("currentSeason").path("transferArrivals");
                JsonNode departures = root.path("currentSeason").path("transferDepartures");

                System.out.println("✅ " + pref.getClubName() + " - " +
                        arrivals.size() + " arrivals & " + departures.size() + " departures");

                newEntries.addAll(saveTransfers(arrivals, "arrival", pref));
                newEntries.addAll(saveTransfers(departures, "departure", pref));

            } catch (Exception e) {
                System.err.println("❌ JSON parse failed for: " + pref.getClubName());
                e.printStackTrace();
            }
        } else {
            System.err.println("❌ API call failed for club: " + pref.getClubName());
        }

        return newEntries;
    }

    private List<NewsEntry> saveTransfers(JsonNode list, String type, UserPreference pref) {
        List<NewsEntry> saved = new ArrayList<>();

        for (JsonNode item : list) {
            try {
                String playerId = item.path("id").asText();
                String clubId = item.path("clubID").asText();
                String fee = item.path("transferFee").asText();
                String dateStr = item.path("date").asText();

                // Check if already present (simple check on player + date + club)
                boolean exists = newsRepo.existsByPlayer_IdAndTransferDateAndClub_Id(
                        playerId,
                        parseDate(dateStr),
                        clubId
                );

                if (exists) continue;

                NewsEntry entry = new NewsEntry();
                entry.setPlayerName(item.path("playerName").asText());
                entry.setPlayerImage(item.path("playerImage").asText());
                entry.setAge(item.path("age").asInt());
                entry.setPosition(item.path("position").asText());
                entry.setPositionsDetail(item.path("positionsdetail").asText());
                entry.setTransferType(type);
                entry.setTransferFee(fee);
                entry.setClubName(item.path("clubName").asText());
                entry.setClubImage(item.path("clubImage").asText());
                entry.setCountryImage(item.path("countryImage").asText());
                entry.setLoan(item.path("loan").asText());
                entry.setRelevant(true);
                entry.setTransferDate(parseDate(dateStr));

                // FK - Club
                Club club = clubRepo.findById(clubId).orElseGet(() -> {
                    Club newClub = new Club();
                    newClub.setId(clubId);
                    newClub.setName(item.path("clubName").asText());
                    newClub.setLogoUrl(item.path("clubImage").asText());
                    return clubRepo.save(newClub);
                });
                entry.setClub(club);

                // FK - Player
                Player player = playerRepo.findById(playerId).orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setId(playerId);
                    newPlayer.setName(item.path("playerName").asText());
                    return playerRepo.save(newPlayer);
                });
                entry.setPlayer(player);

                newsRepo.save(entry);

                // 🧠 Summary + sleep
                String summary = summaryGeneratorService.generateSummary(entry);
                entry.setSummary(summary);

                Thread.sleep(1000); // 1s sleep for API quota
                newsRepo.save(entry); // update with summary

                saved.add(entry);

                // Optional: Notify user
                notificationService.notifyUser(pref.getUser().getId(), summary);

            } catch (Exception e) {
                System.err.println("⚠️ Error saving transfer: " + e.getMessage());
            }
        }

        return saved;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }
}
