# TweetAudit

A Java CLI tool that audits your Twitter/X archive for tweets you may want to delete. It uses Groq's LLM API to evaluate tweets against configurable criteria — forbidden topics, tone, and duplicates — and outputs a CSV of flagged tweets with direct X.com links.

## How it works

1. Reads your Twitter/X data export (`tweets.js`)
2. Detects duplicate tweets locally
3. Sends each tweet to Groq for evaluation against your criteria
4. Writes flagged tweets to a CSV with the reason and URL
5. Checkpoints progress so it can resume if interrupted

## Setup

### 1. Get your Twitter/X archive
- Go to X.com → Settings → Your Account → Download an archive of your data
- Unzip the downloaded file and locate `data/tweets.js`

### 2. Get a Groq API key
- Sign up at [console.groq.com](https://console.groq.com)
- Create an API key and copy it

### 3. Configure the app

Copy the example properties file and add your key:
```
cp app.properties.example app.properties
```

Edit `app.properties`:
```properties
gemini.api.key=gsk_your_groq_key_here
```

Edit `config.json`:
```json
{
  "geminiApiKey": "",
  "archivePath": "C:/path/to/your/data/tweets.js",
  "outputPath": "./output/flagged.csv",
  "checkpointPath": "./output/checkpoint.txt",
  "batchSize": 10,
  "username": "your_x_username",
  "criteria": {
    "forbiddenWords": ["word1", "word2"],
    "toneCheck": true,
    "flagDuplicates": true
  }
}
```

### 4. Run

From IntelliJ: open `Main.java` and click the green play button.

From terminal (requires Maven):
```bash
mvn package
java -jar target/TweetAudit-1.0-SNAPSHOT.jar
```

## Output

Results are written to `output/flagged.csv`:
```
tweet_url,reason
https://x.com/username/status/123,Contains forbidden word: politics
https://x.com/username/status/456,Duplicate tweet
```

## Resuming after a crash

The app saves progress to `output/checkpoint.txt`. If it crashes mid-run, just run it again — it will skip already-processed tweets and continue from where it left off.

To start fresh, delete `output/checkpoint.txt`.

## Configuration options

| Field | Description |
|---|---|
| `archivePath` | Path to your `tweets.js` file |
| `outputPath` | Where to write the flagged CSV |
| `checkpointPath` | Where to store resume progress |
| `batchSize` | Number of tweets per batch |
| `username` | Your X/Twitter handle (no @) |
| `criteria.forbiddenWords` | List of words/topics to flag |
| `criteria.toneCheck` | Flag unprofessional or aggressive tone |
| `criteria.flagDuplicates` | Flag duplicate tweet content |
