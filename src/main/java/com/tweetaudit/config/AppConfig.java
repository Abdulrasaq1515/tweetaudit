package com.tweetaudit.config;

public class AppConfig {
    private  String geminiApiKey = "";
    private  String archivePath = "";
    private  String outputPath = "";
    private  int batchSize = 10;
    private  String  username = "";
    private Criteria criteria;
    private String checkpointPath;



    public AppConfig() {}

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
    }

    public String getArchivePath() {
        return archivePath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public int getBatchSize() {
        return batchSize;
    }
    public String getUsername() {
        return username;
    }
    public Criteria getCriteria() {
        return criteria;
    }
    public String getCheckpointPath() {
        return checkpointPath;
    }

}


