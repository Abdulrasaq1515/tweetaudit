package com.tweetaudit.service;

import com.tweetaudit.model.AuditResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class CsvWriter {
    public void write(List<AuditResult> results, String outputPath, String username) throws IOException {
        Path path = Path.of(outputPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, "tweet_url,reason\n",
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        for (AuditResult result : results) {
            String line = result.getUrl(username) + "," + escapeCsv(result.getAuditReason()) + "\n";
            Files.writeString(path, line, StandardOpenOption.APPEND);
        }
        System.out.println("Done. " + results.size() + " tweet(s) flagged. Output: " + outputPath);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}


