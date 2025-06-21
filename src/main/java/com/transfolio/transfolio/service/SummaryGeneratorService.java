package com.transfolio.transfolio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.transfolio.transfolio.model.NewsEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SummaryGeneratorService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public String generateSummary(NewsEntry entry) {
        try {
            // Convert NewsEntry to JSON string
            String entryJson = mapper.writeValueAsString(entry);

            // Build full prompt
            String promptText = """
                You are a football fan. Create a short, catchy and fun fan-style summary from the JSON below.
                Include player's name, clubs, fee, and whether it’s a loan or permanent move.
                JSON:
                """ + entryJson;

            // Prepare payload as String
            String payload = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(promptText.replace("\"", "\\\"")); // Escape quotes

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Build request
            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            // Final Gemini URL
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            // Call Gemini API
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // Parse and extract response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return mapper.readTree(response.getBody())
                        .path("candidates").get(0)
                        .path("content").path("parts").get(0)
                        .path("text").asText();
            }

        } catch (Exception e) {
            System.err.println("⚠️ Failed to generate AI summary: " + e.getMessage());
        }

        return "⚠️ AI summary could not be generated.";
    }
}
