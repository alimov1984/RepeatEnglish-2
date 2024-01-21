package ru.alimov.repeatenglish.model;

/**
 * UI state of main page.
 */
public class MainActivityUiState {
    private String wordOriginate;
    private String wordTranslated;

    public MainActivityUiState(String wordOriginate, String wordTranslated) {
        this.wordOriginate = wordOriginate;
        this.wordTranslated = wordTranslated;
    }

    public String getWordOriginate() {
        return this.wordOriginate;
    }

    public String getWordTranslated() {
        return this.wordTranslated;
    }
}
