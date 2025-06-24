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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TransferFetcherService {

    private final UserPreferenceRepository preferenceRepo;
    private final NewsEntryRepository newsRepo;
    private final ClubRepository clubRepo;
    private final PlayerRepository playerRepo;
    private final SummaryGeneratorService summaryGeneratorService;
    private final NotificationService notificationService;
    private final String API_URL = "https://transfermarket.p.rapidapi.com/transfers/list-by-club";
    private final String API_KEY;
    private final String API_HOST;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public TransferFetcherService(
            UserPreferenceRepository preferenceRepo,
            NewsEntryRepository newsRepo,
            ClubRepository clubRepo,
            PlayerRepository playerRepo,
            SummaryGeneratorService summaryGeneratorService,
            NotificationService notificationService,
            @Value("${rapid.api.key}") String apiKey,
            @Value("${rapid.api.host}") String apiHost
    ) {
        this.preferenceRepo = preferenceRepo;
        this.newsRepo = newsRepo;
        this.clubRepo = clubRepo;
        this.playerRepo = playerRepo;
        this.summaryGeneratorService = summaryGeneratorService;
        this.notificationService = notificationService;
        this.API_KEY = apiKey;
        this.API_HOST = apiHost;
    }

    public void fetchTransfersForAllUsers() {
        List<UserPreference> preferences = preferenceRepo.findAll();
        for (UserPreference pref : preferences) {
            fetchAndStoreTransfers(pref);
        }
    }

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
                String fee = item.path("transferFee").asText();
                String dateStr = item.path("date").asText();
                LocalDate transferDate = parseDate(dateStr);
                String trackedClubId = pref.getClubIdApi();

                boolean exists = newsRepo.existsByPlayer_IdAndTransferDateAndClub_Id(
                        playerId, transferDate, trackedClubId
                );
                if (exists) continue;

                Club club = clubRepo.findById(trackedClubId).orElseGet(() -> {
                    Club newClub = new Club();
                    newClub.setId(trackedClubId);
                    newClub.setName(pref.getClubName());
                    newClub.setLogoUrl(pref.getLogoUrl());
                    newClub.setCompetitionId(pref.getCompetitionId());
                    return clubRepo.save(newClub);
                });

                Player player = playerRepo.findById(playerId).orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setId(playerId);
                    newPlayer.setName(item.path("playerName").asText());
                    newPlayer.setPosition(item.path("positionsdetail").asText());
                    newPlayer.setClub(club);
                    return playerRepo.save(newPlayer);
                });

                NewsEntry entry = new NewsEntry();
                entry.setPlayerName(item.path("playerName").asText());
                entry.setPlayerImage(item.path("playerImage").asText());
                entry.setAge(item.path("age").asInt());
                entry.setPositionsDetail(item.path("position").asText());
                entry.setPosition(item.path("positionsdetail").asText());
                entry.setTransferType(type);
                entry.setTransferFee(fee);
                entry.setClubName(pref.getClubName());
                entry.setClubImage(pref.getLogoUrl());
                entry.setCountryImage(item.path("countryImage").asText());
                entry.setLoan(item.path("loan").asText());
                entry.setRelevant(true);
                entry.setTransferDate(transferDate);
                entry.setClub(club);
                entry.setPlayer(player);

                NewsEntry savedEntry = newsRepo.save(entry);
                saved.add(savedEntry);

                new Thread(() -> {
                    try {
                        String summary = summaryGeneratorService.generateSummary(savedEntry);
                        savedEntry.setSummary(summary);
                        newsRepo.save(savedEntry);
                        notificationService.notifyUser(pref.getUser().getId(), summary);
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        System.err.println("⚠️ Error in summary thread: " + ex.getMessage());
                    }
                }).start();

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
