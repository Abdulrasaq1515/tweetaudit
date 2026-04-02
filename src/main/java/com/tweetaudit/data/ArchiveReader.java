package com.tweetaudit.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetaudit.model.Tweet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArchiveReader {

    private final ObjectMapper mapper = new ObjectMapper();
    public List<Tweet> readTweets(String archivePath) throws IOException {
        String content = Files.readString(Path.of(archivePath));
        String json = content.substring(content.indexOf('['));
        JsonNode root = mapper.readTree(json);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        List<Tweet> tweets = new ArrayList<>();
        for (JsonNode node : root) {
            JsonNode tweet = node.get("tweet");
            String id = tweet.get("id").asText();
            String text = tweet.get("full_text").asText();
            String dateString = tweet.get("created_at").asText();
            Instant timestamp = ZonedDateTime.parse(dateString, formatter).toInstant();
            tweets.add(new Tweet(id, text, timestamp));
        }
        return tweets;

    }
}
