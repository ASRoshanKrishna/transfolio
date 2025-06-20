package com.transfolio.transfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfolio.transfolio.dto.TransferEntryDTO;
import com.transfolio.transfolio.model.UserPreference;
import com.transfolio.transfolio.repository.NewsEntryRepository;
import com.transfolio.transfolio.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferFetcherService {

    private final UserPreferenceRepository preferenceRepo;
    private final NewsEntryRepository newsRepo;

    private final String API_URL = "https://transfermarket.p.rapidapi.com/transfers/list-by-club";
    private final String API_KEY = "d50f7c3db6msh432bcd5aaf9319fp1023c8jsn4d74f1def565"; // Replace with your own
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
                    JsonNode root = mapper.readTree(response.getBody());
                    JsonNode currentSeason = root.path("currentSeason");

                    List<TransferEntryDTO> arrivals = parseTransfers(currentSeason.path("transferArrivals"));
                    List<TransferEntryDTO> departures = parseTransfers(currentSeason.path("transferDepartures"));

                    System.out.println("✅ " + pref.getClubName() + " - Fetched " + arrivals.size() + " arrivals & "
                            + departures.size() + " departures");

                    // 🔥 Display sample for verification (remove later)
                    if (!arrivals.isEmpty()) {
                        for(TransferEntryDTO i : arrivals) System.out.println("➡️ Arrival: " + i.getPlayerName() + " → " + i.getClubName());
                    }
                    if (!departures.isEmpty()) {
                        for(TransferEntryDTO i : departures) System.out.println("⬅️ Departure: " + i.getPlayerName() + " → " + i.getClubName());
                    }

                    // TODO: Save to DB or return to controller
                } catch (Exception e) {
                    System.err.println("❌ Error parsing JSON for: " + pref.getClubName());
                    e.printStackTrace();
                }
            } else {
                System.out.println("❌ API fetch failed for: " + pref.getClubName());
            }
        }
    }

    private List<TransferEntryDTO> parseTransfers(JsonNode transferArray) {
        List<TransferEntryDTO> list = new ArrayList<>();

        if (transferArray != null && transferArray.isArray()) {
            for (JsonNode node : transferArray) {
                TransferEntryDTO dto = new TransferEntryDTO();
                dto.setId(node.path("id").asText());
                dto.setPlayerName(node.path("playerName").asText());
                dto.setPlayerImage(node.path("playerImage").asText());
                dto.setAge(node.path("age").asInt());
                dto.setPosition(node.path("position").asText());
                dto.setTransferFee(node.path("transferFee").asText());
                dto.setTransferFeeCurrency(node.path("transferFeeCurrency").asText());
                dto.setTransferFeeNumeral(node.path("transferFeeNumeral").asText());
                dto.setTransferFeeUnformatted(node.path("transferFeeUnformatted").asLong());
                dto.setLoan(node.path("loan").asText());
                dto.setDate(node.path("date").asText());
                dto.setPositionsdetail(node.path("positionsdetail").asText());
                dto.setClubID(node.path("clubID").asText());
                dto.setClubName(node.path("clubName").asText());
                dto.setClubImage(node.path("clubImage").asText());
                dto.setCountryImage(node.path("countryImage").asText());

                list.add(dto);
            }
        }

        return list;
    }
}
