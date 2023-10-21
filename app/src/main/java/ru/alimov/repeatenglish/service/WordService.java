package ru.alimov.repeatenglish.service;

import java.util.List;

import ru.alimov.repeatenglish.model.Word;

public interface WordService {

    String insertWord(String wordOriginal, String wordTranslated);

    List<Word> getWordsForChecking();

    boolean incrementIncorrectCheckCounter(String wordOriginal);

    boolean incrementCorrectCheckCounter(String wordOriginal);

    boolean updateDateShowed(String wordOriginal);

}
