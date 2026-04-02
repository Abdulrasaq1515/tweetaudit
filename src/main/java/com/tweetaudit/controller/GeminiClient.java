package com.tweetaudit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetaudit.config.Criteria;
import com.tweetaudit.model.AuditResult;
import com.tweetaudit.model.Tweet;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class GeminiClient {
    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(java.time.Duration.ofSeconds(30))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public String buildPrompt(Tweet tweet, Criteria criteria) {
        return " You are Evaluating Tweet for Deletion. \n " +
                "Flag this Tweet if it violates our rules : \n " +
                "1.It contains or promotes these topics: " + criteria.getForbiddenWords() + "\n" +
                "2.The tone is unprofessional, aggressive, or immature: " + criteria.getToneCheck() + "\n" +
                "\nTweet: \"" + tweet.getTweetContent() + "\"\n" +
                "\nRespond in JSON only, no explanation:\n" +
                "{\"flagged\": true, \"reason\": \"...\"}\n" +
                "or\n" +
                "{\"flagged\": false, \"reason\": \"\"}";
    }

    private String callApi(String prompt, String apiKey) throws IOException, InterruptedException {
        String url = "https://api.groq.com/openai/v1/chat/completions";
        var requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );
        String body = mapper.writeValueAsString(requestBody);
        int maxAttempts = 4;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) return response.body();
                if (response.statusCode() == 400 || response.statusCode() == 401
                        || response.statusCode() == 403 || response.statusCode() == 404) {
                    throw new IOException("Permanent error: HTTP " + response.statusCode());
                }
                long delay = response.headers().firstValue("Retry-After")
                        .map(v -> Long.parseLong(v) * 1000)
                        .orElse((long) Math.pow(2, attempt) * (response.statusCode() == 429 ? 10000 : 4000));
                System.err.println("Status " + response.statusCode() + ". Retrying in " + (delay / 1000) + "s...");
                Thread.sleep(delay);
            } catch (IOException e) {
                System.err.println("Network error: " + e.getClass().getSimpleName() + " - " + e.getMessage() + ". Retrying...");
                if (attempt == maxAttempts - 1) throw e;
                Thread.sleep((long) Math.pow(2, attempt) * 2000);
            }
        }
        throw new IOException("Groq API failed after " + maxAttempts + " attempts");
    }

    public AuditResult evaluate(Tweet tweet, Criteria criteria, String apiKey)
            throws IOException, InterruptedException {
        String prompt = buildPrompt(tweet, criteria);
        String response = callApi(prompt, apiKey);

        JsonNode root = mapper.readTree(response);
        JsonNode choices = root.path("choices");
        if (choices.isEmpty() || choices.get(0) == null) {
            throw new IOException("Groq returned no choices: " + response);
        }
        String text = choices.get(0).path("message").path("content").asText();

        text = text.replace("```json", "").replace("```", "").trim();

        JsonNode result = mapper.readTree(text);
        boolean flagged = result.path("flagged").asBoolean();
        String reason = result.path("reason").asText();

        return new AuditResult(tweet, flagged, reason);
    }
}
