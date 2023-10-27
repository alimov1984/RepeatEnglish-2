package ru.alimov.repeatenglish.service;

import java.util.List;
import java.util.Optional;

import ru.alimov.repeatenglish.model.Word;

public interface WordService {

    String insertWord(String wordOriginal, String wordTranslated);

    List<Word> getWordsForChecking();

    boolean incrementIncorrectCheckCounter(String wordOriginal);

    boolean incrementCorrectCheckCounter(String wordOriginal);

    boolean updateDateShowed(String wordOriginal);

    List<Word> getWordsForExport();

    Optional<Long> insertWord(Word word);

    void clearWordTable();

}
