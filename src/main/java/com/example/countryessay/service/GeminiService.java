// File: src/main/java/com/example/essay/service/GeminiService.java
package com.example.countryessay.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class GeminiService {

    private final String apiKey;

    public GeminiService() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new RuntimeException("Environment variable GEMINI_API_KEY is not set.");
        }
    }

    public String getEssay(String country) {
        String prompt = "Write a 400-word essay on " + country;

        WebClient client = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-pro-exp:generateContent")
                .build();

        try {
            Map response = client.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .header("Content-Type", "application/json")
                    .bodyValue("{" +
                            "\"contents\": [{\"parts\": [{\"text\": \"" + prompt + "\"}]}]}")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                var contentList = (java.util.List<Map<String, Object>>) response.get("candidates");
                if (!contentList.isEmpty()) {
                    var content = contentList.get(0);
                    var partsList = (java.util.List<Map<String, Object>>) ((Map<String, Object>) content.get("content")).get("parts");
                    if (!partsList.isEmpty()) {
                        return partsList.get(0).get("text").toString();
                    }
                }
            }
        } catch (WebClientResponseException e) {
            return "Error: " + e.getResponseBodyAsString();
        }

        return "No response from Gemini API.";
    }
}
