package com.tweetaudit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CheckpointStoreTest {
    @TempDir
    Path tempDir;
    CheckpointStore  checkpointStore = new CheckpointStore();
    @Test
    public void missingFileReturnEmpty() throws IOException {
        String path = tempDir.resolve("checkpoint").toString();
            Set<String> result = checkpointStore.loadProcessed(path);
            assertTrue(result.isEmpty());

    }

    @Test
    public void markProcessedThenLoadContainsId() throws IOException {
        String path = tempDir.resolve("checkpoint.txt").toString();
        checkpointStore.markProcessed(path, "1001");
        Set<String> result = checkpointStore.loadProcessed(path);
        assertTrue(result.contains("1001"));
    }

    @Test
    public void isProcessedReturnsTrueForKnownIdFalseForUnknown() throws IOException {
        String path = tempDir.resolve("checkpoint.txt").toString();
        checkpointStore.markProcessed(path, "1001");
        Set<String> result = checkpointStore.loadProcessed(path);
        assertTrue(checkpointStore.isProcessed(result, "1001"));
        assertFalse(checkpointStore.isProcessed(result, "9999"));
    }



}