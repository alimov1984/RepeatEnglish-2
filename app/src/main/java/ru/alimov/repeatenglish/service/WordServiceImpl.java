package ru.alimov.repeatenglish.service;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import ru.alimov.repeatenglish.model.Word;
import ru.alimov.repeatenglish.repository.WordRepository;

public class WordServiceImpl implements WordService {

    private static final int DEFAULT_WORD_CHECKING_COUNT = 10;
    Context context;
    private WordRepository wordRepository;

    public WordServiceImpl(Context context) {
        this.context = context;
        this.wordRepository = new WordRepository(context);
    }


    public String insertWord(String wordOriginal, String wordTranslated) {
        String result = "";
        Word word = wordRepository.getWordByOriginal(wordOriginal);
        if (word != null) {
            word.setDateUpdated(Instant.now());
            word.setAddCounter(word.getAddCounter() + 1);
            word.setRating(getRatingValue(word));
            Optional<Integer> updateResult = wordRepository.updateWord(word);
            if (updateResult.isPresent() && updateResult.get() > 0) {
                result = "Update word successfully";
            }
        } else {
            Instant now = Instant.now();
            Word newWord = new Word(wordOriginal,
                    wordTranslated,
                    now,
                    now,
                    null,
                    0,
                    0,
                    0,
                    0);
            newWord.setRating(getRatingValue(newWord));
            Optional<Long> insertResult = wordRepository.insertWord(word);
            if (insertResult.isPresent() && insertResult.get() > 0) {
                result = "Insert word successfully:";
            }
        }
        return result;
    }

    public List<Word> getWordsForChecking() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int wordCount = prefs.getInt("word_checking_count", DEFAULT_WORD_CHECKING_COUNT);
        List<Word> wordList = wordRepository.getWordsForChecking(10);
        return wordList;
    }

    public boolean incrementCorrectCheckCounter(String wordOriginal) {
        Word word = wordRepository.getWordByOriginal(wordOriginal);
        if (word != null) {
            word.setCorrectCheckCounter(word.getCorrectCheckCounter() + 1);
            word.setRating(getRatingValue(word));
            Optional<Integer> result = wordRepository.incrementWordCorrectCheckCounter(word);
            if (result.isPresent() && result.get() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean incrementIncorrectCheckCounter(String wordOriginal) {
        Word word = wordRepository.getWordByOriginal(wordOriginal);
        if (word != null) {
            word.setIncorrectCheckCounter(word.getIncorrectCheckCounter() + 1);
            word.setRating(getRatingValue(word));
            Optional<Integer> result = wordRepository.incrementWordIncorrectCheckCounter(word);
            if (result.isPresent() && result.get() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean updateDateShowed(String wordOriginal) {
        Word word = wordRepository.getWordByOriginal(wordOriginal);
        if (word != null) {
            Optional<Integer> result = wordRepository.updateWordDateShowed(word);
            if (result.isPresent() && result.get() > 0) {
                return true;
            }
        }
        return false;
    }

    private long getRatingValue(Word word) {
        long result = 1000;
        long k1 = word.getAddCounter();
        long k2 = (ZonedDateTime.now().getYear() -
                word.getDateUpdated().atZone(ZoneId.systemDefault()).getYear()) * 12 +
                ZonedDateTime.now().getMonthValue() -
                word.getDateUpdated().atZone(ZoneId.systemDefault()).getMonthValue();
        long k3 = word.getCorrectCheckCounter();
        long k4 = word.getIncorrectCheckCounter();
        long k5 = word.getWordOriginal().length();

        result = result + k1 - k2 - k3 + k4 + k5;
        return result;
    }
}
