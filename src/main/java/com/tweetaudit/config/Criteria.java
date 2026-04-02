package com.tweetaudit.config;

import java.util.List;

public class Criteria {
    private List<String> forbiddenWords;
    private boolean toneCheck;
    private boolean flagDuplicates;

    public Criteria() {}

    public List<String> getForbiddenWords() {
        return forbiddenWords;
    }
    public Boolean getToneCheck() {
        return toneCheck;
    }
    public Boolean isFlagDuplicates() {
        return flagDuplicates;
    }

}
