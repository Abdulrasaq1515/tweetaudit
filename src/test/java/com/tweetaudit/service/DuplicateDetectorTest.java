package com.tweetaudit.service;

import com.tweetaudit.model.AuditResult;
import com.tweetaudit.model.Tweet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateDetectorTest {

    @Test
    public void twoIdenticalTweets_oneFlaggedAsDuplicate() {
        DuplicateDetector detector = new DuplicateDetector();
        List<Tweet> tweets = List.of(
                new Tweet("1001", "crypto is great", Instant.now()),
                new Tweet("1002", "crypto is great", Instant.now())
        );

        List<AuditResult> results = detector.detect(tweets);

        assertEquals(1, results.size());
        assertTrue(results.get(0).isFlagged());
        assertEquals("Duplicate tweet", results.get(0).getAuditReason());
    }

    @Test
    public void detectionTestWithDifferentContent()  {
        DuplicateDetector detector = new DuplicateDetector();
        List<Tweet> tweets = List.of(
                new Tweet("1001","i love twitter", Instant.now()),
                new Tweet("1002","i love x",  Instant.now())
        );

        List<AuditResult> results = detector.detect(tweets);

        assertTrue(results.isEmpty());
    }

    @Test
    public void DetectionTestWithSameContentDifferentCase()  {
        DuplicateDetector detector = new DuplicateDetector();
        List<Tweet> tweets = List.of(
                new Tweet("1001","Money is Good",Instant.now()),
                new Tweet("1002","money is good",Instant.now())
        );

        List<AuditResult> results = detector.detect(tweets);

        assertEquals(1, results.size());
        assertTrue(results.get(0).isFlagged());
        assertEquals("Duplicate tweet", results.get(0).getAuditReason());
    }

}