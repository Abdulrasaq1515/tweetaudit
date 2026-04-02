package com.tweetaudit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetaudit.config.AppConfig;
import com.tweetaudit.controller.GeminiClient;
import com.tweetaudit.data.ArchiveReader;
import com.tweetaudit.service.AuditEngine;
import com.tweetaudit.service.CheckpointStore;
import com.tweetaudit.service.CsvWriter;
import com.tweetaudit.service.DuplicateDetector;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
public class Main {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AppConfig config = mapper.readValue(new File("config.json"), AppConfig.class);

        String apiKey = null;
        File propsFile = new File("app.properties");
        if (propsFile.exists()) {
            Properties props = new Properties();
            props.load(new FileInputStream(propsFile));
            apiKey = props.getProperty("gemini.api.key");
        }
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("GEMINI_API_KEY");
        }
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = config.getGeminiApiKey();
        }
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Error: Gemini API key not set. Add it to app.properties or set GEMINI_API_KEY env var.");
            System.exit(1);
        }
        config.setGeminiApiKey(apiKey);

        AuditEngine engine = new AuditEngine(
                new ArchiveReader(),
                new GeminiClient(),
                new CheckpointStore(),
                new CsvWriter(),
                config.getCriteria(),
                new DuplicateDetector(),
                config
        );

        engine.run();
    }

}