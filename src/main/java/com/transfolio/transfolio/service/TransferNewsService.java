package com.transfolio.transfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.jconsole.JConsoleContext;
import com.transfolio.transfolio.dto.TransferRumorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferNewsService {

    @Autowired
    private RestTemplate restTemplate;

    private final String apiKey = "d50f7c3db6msh432bcd5aaf9319fp1023c8jsn4d74f1def565";
    private final String apiHost = "transfermarket.p.rapidapi.com";

    public List<TransferRumorDTO> fetchTransferRumors(String clubId, String competitionId) {
        String url = "https://transfermarket.p.rapidapi.com/transfers/list-rumors?clubIds=" + clubId +
                "&competitionIds=" + competitionId + "&sort=date_desc&domain=com";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<TransferRumorDTO> resultList = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode rumors = root.path("rumors");
                System.out.println("Rumors from response " + rumors);
                for (JsonNode item : rumors) {
                    String fromId = item.path("fromClubID").asText();
                    String toId = item.path("toClubID").asText();

                    if (clubId.equals(fromId) || clubId.equals(toId)) {
                        TransferRumorDTO dto = new TransferRumorDTO();
                        dto.setId(item.path("id").asText());
                        dto.setPlayerID(item.path("playerID").asText());
                        dto.setFromClubID(fromId);
                        dto.setToClubID(toId);
                        dto.setClosed(item.path("isClosed").asBoolean());
                        dto.setProbability(item.path("probability").asText());
                        dto.setProgression(item.path("progression").asText());
                        dto.setThreadUrl(item.path("threadUrl").asText());
                        dto.setLang(item.path("lang").asText());
                        dto.setLastPostDate(item.path("lastPostDate").asLong());
                        dto.setClosedType(item.path("closedType").asText());
                        dto.setMarketValue(item.path("marketValue").path("value").asLong());
                        dto.setCurrency(item.path("marketValue").path("currency").asText());
                        System.out.println("Rumordto " + dto);
                        resultList.add(dto);
                    }
                }

            } catch (Exception e) {
                System.err.println("❌ Error parsing rumors response");
                e.printStackTrace();
            }
        }

        return resultList;
    }
}
