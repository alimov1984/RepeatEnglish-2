package ru.alimov.repeatenglish.model;

import java.time.Instant;

public class Word {
    private long id;
    private String wordOriginal;
    private String wordTranslated;
    private Instant dateCreated;
    private Instant dateUpdated;
    private Instant dateShowed;
    private long addCounter;
    private long correctCheckCounter;
    private long incorrectCheckCounter;
    private long rating;

    public Word(String wordOriginal, String wordTranslated, Instant dateCreated,
                Instant dateUpdated, Instant dateShowed, long updateCounter,
                long correctCheckCounter, long incorrectCheckCounter, long rating) {
        this.wordOriginal = wordOriginal;
        this.wordTranslated = wordTranslated;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.dateShowed = dateShowed;
        this.addCounter = updateCounter;
        this.correctCheckCounter = correctCheckCounter;
        this.incorrectCheckCounter = incorrectCheckCounter;
        this.rating = rating;
    }

    public Word(long id, String wordOriginal, String wordTranslated, Instant dateCreated,
                Instant dateUpdated, Instant dateShowed, long updateCounter,
                long correctCheckCounter, long incorrectCheckCounter, long rating) {
        this.id = id;
        this.wordOriginal = wordOriginal;
        this.wordTranslated = wordTranslated;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.dateShowed = dateShowed;
        this.addCounter = updateCounter;
        this.correctCheckCounter = correctCheckCounter;
        this.incorrectCheckCounter = incorrectCheckCounter;
        this.rating = rating;
    }

    public long getId() {
        return this.id;
    }

    public String getWordOriginal() {
        return this.wordOriginal;
    }

    public String getWordTranslated() {
        return this.wordTranslated;
    }

    public Instant getDateCreated() {
        return this.dateCreated;
    }

    public Instant getDateUpdated() {
        return this.dateUpdated;
    }

    public Instant setDateUpdated(Instant dateUpdated) {
        this.dateUpdated = dateUpdated;
        return this.dateUpdated;
    }

    public Instant getDateShowed() {
        return this.dateShowed;
    }

    public long getAddCounter() {
        return this.addCounter;
    }

    public long setAddCounter(long addCounter) {
        this.addCounter = addCounter;
        return this.addCounter;
    }

    public long getCorrectCheckCounter() {
        return this.correctCheckCounter;
    }

    public long getIncorrectCheckCounter() {
        return this.incorrectCheckCounter;
    }

    public long getRating() {
        return this.rating;
    }

    public long setRating(long rating) {
        this.rating = rating;
        return this.rating;
    }

}