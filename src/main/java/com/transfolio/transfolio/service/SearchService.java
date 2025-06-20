package com.transfolio.transfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfolio.transfolio.dto.SearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private RestTemplate restTemplate;

    private final String apiKey = "d50f7c3db6msh432bcd5aaf9319fp1023c8jsn4d74f1def565";
    private final String apiHost = "transfermarket.p.rapidapi.com";

    public List<SearchResultDTO> searchClubs(String query) {
        String url = "https://transfermarket.p.rapidapi.com/search?query=" + query + "&domain=com";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<SearchResultDTO> results = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode clubs = root.path("clubs");
            for (JsonNode club : clubs) {
                SearchResultDTO dto = new SearchResultDTO();
                dto.setId(club.path("id").asText());
                dto.setName(club.path("name").asText());
                dto.setLogoUrl(club.path("logoImage").asText());
                dto.setCompetitionId(club.path("competitionID").asText());
                dto.setCompetitionName(club.path("competitionName").asText());
                results.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

}
