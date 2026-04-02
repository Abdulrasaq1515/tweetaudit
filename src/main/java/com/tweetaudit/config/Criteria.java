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
    public void setForbiddenWords(List<String> forbiddenWords) {
        this.forbiddenWords = forbiddenWords;
    }
    public Boolean getToneCheck() {
        return toneCheck;
    }
    public void setToneCheck(boolean toneCheck) {
        this.toneCheck = toneCheck;
    }
    public Boolean isFlagDuplicates() {
        return flagDuplicates;
    }
    public void setFlagDuplicates(boolean flagDuplicates) {
        this.flagDuplicates = flagDuplicates;
    }

}
