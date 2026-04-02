package com.tweetaudit.model;

public class AuditResult {
    private Tweet tweet;
    private boolean isFlagged;
    private String auditReason;


    public AuditResult(Tweet tweet, boolean isFlagged, String auditReason) {
        this.tweet = tweet;
        this.isFlagged = isFlagged;
        this.auditReason = auditReason;
    }

    public String getUrl(String username) {
        return "https://x.com/" + username + "/status/" + tweet.getTweetId();

    }

    public Tweet getTweet() {
        return tweet;
    }
    public boolean isFlagged() {
        return isFlagged;
    }
    public String getAuditReason() {
        return auditReason;
    }

}
