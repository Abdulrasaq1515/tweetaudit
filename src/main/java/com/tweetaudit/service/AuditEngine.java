package com.tweetaudit.service;

import com.tweetaudit.config.AppConfig;
import com.tweetaudit.config.Criteria;
import com.tweetaudit.controller.GeminiClient;
import com.tweetaudit.data.ArchiveReader;
import com.tweetaudit.model.AuditResult;
import com.tweetaudit.model.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AuditEngine {
    private final ArchiveReader archiveReader;
    private final GeminiClient geminiClient;
    private  final CheckpointStore checkpointStore;
    private final CsvWriter csvWriter;
    private final Criteria criteria;
    private final DuplicateDetector duplicateDetector;
    private final AppConfig appConfig;


    public AuditEngine(ArchiveReader archiveReader, GeminiClient geminiClient, CheckpointStore checkpointStore , CsvWriter csvWriter, Criteria criteria, DuplicateDetector duplicateDetector, AppConfig appConfig) {
        this.archiveReader = archiveReader;
        this.geminiClient = geminiClient;
        this.checkpointStore = checkpointStore;
        this.csvWriter = csvWriter;
        this.criteria = criteria;
        this.appConfig = appConfig;
        this.duplicateDetector = duplicateDetector;

    }
    public void run() throws IOException, InterruptedException {
        List<Tweet> tweets = archiveReader.readTweets(appConfig.getArchivePath());

        Set<String> processed = checkpointStore.loadProcessed(appConfig.getCheckpointPath());

        List<AuditResult> duplicates = criteria.isFlagDuplicates()
                ? duplicateDetector.detect(tweets)
                : new ArrayList<>();
        Set<String> duplicateIds = duplicates.stream()
                .map(r -> r.getTweet().getTweetId())
                .collect(Collectors.toSet());

        List<AuditResult> geminiResults = new ArrayList<>();
        int batchSize = appConfig.getBatchSize();
        for (int i = 0; i < tweets.size(); i += batchSize) {
            int end = Math.min(i + batchSize, tweets.size());
            List<Tweet> batch = tweets.subList(i, end);
            for (Tweet tweet : batch) {
                if (checkpointStore.isProcessed(processed, tweet.getTweetId())) continue;
                if (duplicateIds.contains(tweet.getTweetId())) continue;
                AuditResult result = geminiClient.evaluate(tweet, appConfig.getCriteria(), appConfig.getGeminiApiKey());
                geminiResults.add(result);
                checkpointStore.markProcessed(appConfig.getCheckpointPath(), tweet.getTweetId());
                Thread.sleep(5500); // stay under 15 req/min free tier limit
            }
        }

        List<AuditResult> allFlagged = new ArrayList<>();
        allFlagged.addAll(duplicates);
        allFlagged.addAll(geminiResults.stream()
                .filter(AuditResult::isFlagged)
                .collect(Collectors.toList()));

        csvWriter.write(allFlagged, appConfig.getOutputPath(), appConfig.getUsername());
    }

}
