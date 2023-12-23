package ru.alimov.repeatenglish.model;

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

//    public void setWordOriginate(String wordOriginate) {
//        this.wordOriginate = wordOriginate;
//    }
//
//    public void getWordTranslated(String wordTranslated) {
//        this.wordTranslated = wordTranslated;
//    }
}
