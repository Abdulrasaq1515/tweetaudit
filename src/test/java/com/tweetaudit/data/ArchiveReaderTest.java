package com.tweetaudit.data;

import com.tweetaudit.model.Tweet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArchiveReaderTest {
    @TempDir
    public Path tempDir;
    @Test
    public void testReadTweets() throws IOException {
        String content = "window.YTD.tweets.part0 = [{\"tweet\":{\"id\":\"1001\",\"full_text\":\"Hello world\",\"created_at\":\"Mon Jan 01 12:00:00 +0000 2024\"}}]";
        Path file = tempDir.resolve("tweets.js");
        Files.writeString(file, content);

        ArchiveReader reader = new ArchiveReader();
        List<Tweet> tweets = reader.readTweets(file.toString());

        assertEquals(1, tweets.size());
        assertEquals("1001", tweets.get(0).getTweetId());
        assertEquals("Hello world", tweets.get(0).getTweetContent());
    }

    @Test
    public void testDuplicateContent() throws IOException {
        String content = "window.YTD.tweets.part0 = [" +
                "{\"tweet\":{\"id\":\"1001\",\"full_text\":\"Hello world\",\"created_at\":\"Mon Jan 01 12:00:00 +0000 2024\"}}," +
                "{\"tweet\":{\"id\":\"1002\",\"full_text\":\"Hello world\",\"created_at\":\"Tue Jan 02 12:00:00 +0000 2024\"}}" +
                "]";
        Path file = tempDir.resolve("tweets.js");
        Files.writeString(file, content);

        ArchiveReader reader = new ArchiveReader();
        List<Tweet> tweets = reader.readTweets(file.toString());
        assertEquals(2, tweets.size());
    }


}