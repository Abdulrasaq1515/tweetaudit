package com.tweetaudit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckpointStore {

    public Set<String> loadProcessed(String checkpointPath) throws IOException {
        if(!Files.exists(Path.of(checkpointPath))) {
            return new HashSet<>();

        }
        List<String> lines = Files.readAllLines(Path.of(checkpointPath));
        return new HashSet<>(lines);
    }

    public void markProcessed(String checkpointPath, String tweetId) throws IOException {
        Path path = Path.of(checkpointPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, tweetId + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public boolean isProcessed(Set<String> processed, String tweetId) {
        return processed.contains(tweetId);

    }
}
