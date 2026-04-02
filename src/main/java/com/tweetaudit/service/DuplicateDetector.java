package com.tweetaudit.service;

import com.tweetaudit.model.AuditResult;
import com.tweetaudit.model.Tweet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateDetector {

    public List<AuditResult> detect(List<Tweet> tweets) {
        List<AuditResult> results = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Tweet tweet : tweets) {
            String content = tweet.getTweetContent();
            String normalised = content.toLowerCase().trim();
            if (seen.contains(normalised)) {
                results.add(new AuditResult(tweet, true, "Duplicate tweet"));
            }
            seen.add(normalised);

        }
        return results;
    }
}
