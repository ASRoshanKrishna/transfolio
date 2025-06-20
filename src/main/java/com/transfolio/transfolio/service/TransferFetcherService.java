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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TransferFetcherService {

    private final UserPreferenceRepository preferenceRepo;
    private final NewsEntryRepository newsRepo;
    private final ClubRepository clubRepo;
    private final PlayerRepository playerRepo;

    private final String API_URL = "https://transfermarket.p.rapidapi.com/transfers/list-by-club";
    private final String API_KEY = "d50f7c3db6msh432bcd5aaf9319fp1023c8jsn4d74f1def565";
    private final String API_HOST = "transfermarket.p.rapidapi.com";

    public void fetchTransfersForAllUsers() {
        List<UserPreference> preferences = preferenceRepo.findAll();
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        for (UserPreference pref : preferences) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", API_KEY);
            headers.set("X-RapidAPI-Host", API_HOST);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = API_URL + "?id=" + pref.getClubIdApi()
                    + "&seasonID=2025"
                    + "&domain=com";

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                try {
                    String json = response.getBody();
                    JsonNode root = mapper.readTree(json);
                    JsonNode arrivals = root.path("currentSeason").path("transferArrivals");
                    JsonNode departures = root.path("currentSeason").path("transferDepartures");

                    System.out.println("✅ " + pref.getClubName() + " - Fetched " + arrivals.size() + " arrivals & "
                            + departures.size() + " departures");

                    saveTransfers(arrivals, "arrival");
                    saveTransfers(departures, "departure");

                } catch (Exception e) {
                    System.err.println("❌ Error parsing JSON for: " + pref.getClubName());
                    e.printStackTrace();
                }
            } else {
                System.out.println("❌ API fetch failed for: " + pref.getClubName());
            }
        }
    }

    private void saveTransfers(JsonNode list, String type) {
        for (JsonNode item : list) {
            try {
                NewsEntry entry = new NewsEntry();
                entry.setPlayerName(item.path("playerName").asText());
                entry.setPlayerImage(item.path("playerImage").asText());
                entry.setAge(item.path("age").asInt());
                entry.setPosition(item.path("position").asText());
                entry.setPositionsDetail(item.path("positionsdetail").asText());
                entry.setTransferType(type);
                entry.setTransferFee(item.path("transferFee").asText());
                entry.setClubName(item.path("clubName").asText());
                entry.setClubImage(item.path("clubImage").asText());
                entry.setCountryImage(item.path("countryImage").asText());
                entry.setLoan(item.path("loan").asText());
                entry.setRelevant(true);

                // Parse and set date
                String dateStr = item.path("date").asText();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
                    entry.setTransferDate(LocalDate.parse(dateStr, formatter));
                } catch (Exception e) {
                    entry.setTransferDate(null);
                }

                // Set club (FK)
                String clubId = item.path("clubID").asText();
                Club club = clubRepo.findById(clubId).orElseGet(() -> {
                    Club newClub = new Club();
                    newClub.setId(clubId);
                    newClub.setName(item.path("clubName").asText());
                    newClub.setLogoUrl(item.path("clubImage").asText());
                    return clubRepo.save(newClub);
                });
                entry.setClub(club);

                // Set player (FK)
                String playerId = item.path("id").asText();
                Player player = playerRepo.findById(playerId).orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setId(playerId);
                    newPlayer.setName(item.path("playerName").asText());
                    return playerRepo.save(newPlayer);
                });
                entry.setPlayer(player);

                newsRepo.save(entry);

            } catch (Exception e) {
                System.err.println("⚠️ Skipped entry due to error: " + e.getMessage());
            }
        }
    }
}
