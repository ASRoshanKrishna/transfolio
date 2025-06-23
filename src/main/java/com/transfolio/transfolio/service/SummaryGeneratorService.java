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

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    // Used for transfers
    public String generateSummary(NewsEntry entry) {
        try {
            String entryJson = mapper.writeValueAsString(entry);

            String promptText = """
                You are a football fan. Create a short, catchy and fun fan-style summary from the JSON below.
                Include player's name, clubs, fee, and whether it’s a loan or permanent move.
                JSON:
                """ + entryJson;

            return sendToGemini(promptText);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to generate transfer summary: " + e.getMessage());
            return "⚠️ AI summary could not be generated.";
        }
    }

    // NEW: Used for rumors
    public String generateRumorSummary(String rumorContext) {
        try {
            String prompt = """
                You're a football insider like Fabrizio Romano, creating summaries for transfer rumors. Here is the rumor info:

                %s

                First, PLS DON'T START WITH RESPONDING TO ME. Now check whether the transfer happened & its no longer just a 
                rumor. Now write a(max 5 lines) fun, exciting, human-style content for fans to understand this rumor, just as 
                Romano does. Search and include player name, position, possible clubs, fees, release clause, when transfer to 
                be expected etc. from context.
                """.formatted(rumorContext);

            return sendToGemini(prompt);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to generate rumor summary: " + e.getMessage());
            return "⚠️ Rumor summary could not be generated.";
        }
    }

    // Extracted Gemini API call
    private String sendToGemini(String promptText) {
        try {
            // ✅ Delay to respect Gemini Free Tier rate limit (15 requests/min)
            Thread.sleep(3000); // 1 second delay before each API call

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
            """.formatted(promptText.replace("\"", "\\\""));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            String url = GEMINI_URL + apiKey;

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return mapper.readTree(response.getBody())
                        .path("candidates").get(0)
                        .path("content").path("parts").get(0)
                        .path("text").asText();
            }

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("⚠️ Gemini API error: " + e.getMessage());
        }

        return "⚠️ Gemini summary could not be generated.";
    }

}
