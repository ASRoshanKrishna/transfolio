package com.transfolio.transfolio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TransferNewsService {

    @Autowired
    private RestTemplate restTemplate;

    private final String apiKey = "d50f7c3db6msh432bcd5aaf9319fp1023c8jsn4d74f1def565";
    private final String apiHost = "transfermarket.p.rapidapi.com";

    public String fetchTransferRumors() {
        String url = "https://transfermarket.p.rapidapi.com/transfers/list-rumors?clubIds=131&competitionIds=ES1&sort=date_desc&domain=com";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
