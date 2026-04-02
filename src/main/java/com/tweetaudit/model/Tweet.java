package com.tweetaudit.model;

import java.time.Instant;

public class Tweet {
    private String tweetId;
    private String tweetContent;
    private Instant tweetTimestamp;


    public Tweet(String tweetId, String tweetContent, Instant tweetTimestamp) {
        this.tweetId = tweetId;
        this.tweetContent = tweetContent;
        this.tweetTimestamp = tweetTimestamp;
    }

    public String getTweetId() {
        return tweetId;
    }

    public String getTweetContent() {
        return tweetContent;
    }

    public Instant getTweetTimestamp() {
        return tweetTimestamp;
    }

    @Override
    public String toString() {
        return tweetId + " " + tweetContent + " " + tweetTimestamp + " ";
    }

}
