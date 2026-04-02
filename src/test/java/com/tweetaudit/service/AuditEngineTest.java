package com.tweetaudit.service;

import com.tweetaudit.config.AppConfig;
import com.tweetaudit.config.Criteria;
import com.tweetaudit.controller.GeminiClient;
import com.tweetaudit.data.ArchiveReader;
import com.tweetaudit.model.AuditResult;
import com.tweetaudit.model.Tweet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditEngineTest {

    @TempDir
    Path tempDir;

    // Stub ArchiveReader that returns a fixed list of tweets
    static class StubArchiveReader extends ArchiveReader {
        private final List<Tweet> tweets;
        StubArchiveReader(List<Tweet> tweets) { this.tweets = tweets; }
        @Override
        public List<Tweet> readTweets(String path) { return tweets; }
    }

    // Stub GeminiClient that flags tweets containing "bad"
    static class StubGeminiClient extends GeminiClient {
        @Override
        public AuditResult evaluate(Tweet tweet, Criteria criteria, String apiKey) {
            boolean flagged = tweet.getTweetContent().contains("bad");
            return new AuditResult(tweet, flagged, flagged ? "Contains bad content" : "");
        }
    }

    private AppConfig makeConfig(Path tempDir) {
        AppConfig config = new AppConfig();
        config.setGeminiApiKey("test-key");
        config.setArchivePath(tempDir.resolve("tweets.js").toString());
        config.setOutputPath(tempDir.resolve("flagged.csv").toString());
        config.setCheckpointPath(tempDir.resolve("checkpoint.txt").toString());
        config.setBatchSize(10);
        config.setUsername("testuser");
        Criteria criteria = new Criteria();
        criteria.setForbiddenWords(List.of("bad"));
        criteria.setToneCheck(false);
        criteria.setFlagDuplicates(true);
        config.setCriteria(criteria);
        return config;
    }

    @Test
    void flaggedTweetsAreWrittenToCsv() throws Exception {
        List<Tweet> tweets = List.of(
                new Tweet("1", "hello world", Instant.now()),
                new Tweet("2", "this is bad content", Instant.now())
        );
        AppConfig config = makeConfig(tempDir);

        AuditEngine engine = new AuditEngine(
                new StubArchiveReader(tweets),
                new StubGeminiClient(),
                new CheckpointStore(),
                new CsvWriter(),
                config.getCriteria(),
                new DuplicateDetector(),
                config
        );

        engine.run();

        String csv = Files.readString(Path.of(config.getOutputPath()));
        assertTrue(csv.contains("status/2"), "flagged tweet should be in CSV");
        assertFalse(csv.contains("status/1"), "clean tweet should not be in CSV");
    }

    @Test
    void alreadyProcessedTweetsAreSkipped() throws Exception {
        List<Tweet> tweets = List.of(
                new Tweet("1", "bad tweet", Instant.now())
        );
        AppConfig config = makeConfig(tempDir);

        // Pre-populate checkpoint with tweet "1"
        Files.createDirectories(tempDir);
        Files.writeString(tempDir.resolve("checkpoint.txt"), "1\n");

        AuditEngine engine = new AuditEngine(
                new StubArchiveReader(tweets),
                new StubGeminiClient(),
                new CheckpointStore(),
                new CsvWriter(),
                config.getCriteria(),
                new DuplicateDetector(),
                config
        );

        engine.run();

        String csv = Files.readString(Path.of(config.getOutputPath()));
        // tweet "1" was checkpointed so should not appear in flagged output
        assertFalse(csv.contains("status/1"), "checkpointed tweet should be skipped");
    }

    @Test
    void duplicateTweetsAreFlagged() throws Exception {
        List<Tweet> tweets = List.of(
                new Tweet("1", "hello world", Instant.now()),
                new Tweet("2", "hello world", Instant.now())
        );
        AppConfig config = makeConfig(tempDir);

        AuditEngine engine = new AuditEngine(
                new StubArchiveReader(tweets),
                new StubGeminiClient(),
                new CheckpointStore(),
                new CsvWriter(),
                config.getCriteria(),
                new DuplicateDetector(),
                config
        );

        engine.run();

        String csv = Files.readString(Path.of(config.getOutputPath()));
        assertTrue(csv.contains("status/2"), "duplicate tweet should be flagged");
    }
}
