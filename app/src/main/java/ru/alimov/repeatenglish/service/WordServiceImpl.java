package ru.alimov.repeatenglish.service;

import static android.content.Context.MODE_PRIVATE;
import static ru.alimov.repeatenglish.util.Const.PREFERENCE_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
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
            Optional<Long> insertResult = wordRepository.insertWord(newWord);
            if (insertResult.isPresent() && insertResult.get() > 0) {
                result = "Insert word successfully:";
            }
        }
        return result;
    }

    public List<Word> getWordsForChecking() {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        int wordCount = settings.getInt("word_checking_count", DEFAULT_WORD_CHECKING_COUNT);
        List<Word> wordList = wordRepository.getWordsForChecking(wordCount);
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

    public List<Word> getWordsForExport() {
        List<Word> wordList = wordRepository.getWordsForExport();
        return wordList;
    }

    public Optional<Long> insertWord(Word word) {
        Optional<Long> affected = wordRepository.insertWord(word);
        return affected;
    }

    public void clearWordTable() {
        wordRepository.clearWordTable();
    }
/*
    public void exportDbInFile() {
        List<Word> wordList = getWordsForExport();
        if (wordList.size() == 0) {
            return;
        }
        String fileName = String.format("repeatEnglish_{%s}_{%s}_{%s}_{%s}_{%s}_{%s}.csv"
                , LocalDateTime.now().getDayOfMonth()
                , LocalDateTime.now().getMonthValue()
                , LocalDateTime.now().getYear()
                , LocalDateTime.now().getHour()
                , LocalDateTime.now().getMinute()
                , LocalDateTime.now().getSecond());

        try (FileOutputStream fileOutputStream = context.openFileOutput("export/" + fileName, MODE_PRIVATE);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream)) {
            outputStreamWriter.write("Id,WordOriginal,WordTranslated,DateCreated,DateUpdated,DateShowed,AddCounter,CorrectCheckCounter,IncorrectCheckCounter,Rating\n");
            for (Word word : wordList) {
                outputStreamWriter.write(String.format("{%s},{%s},{%s},{%s},{%s},{%s},{%s,{%s},{%s},{%s}\n"
                        , word.getId()
                        , word.getWordOriginal()
                        , word.getWordTranslated()
                        , word.getDateCreated().toString()
                        , word.getDateUpdated().toString()
                        , word.getDateShowed() != null ? word.getDateShowed().toString() : "0"
                        , word.getAddCounter()
                        , word.getCorrectCheckCounter()
                        , word.getIncorrectCheckCounter()
                        , word.getRating()));
            }
        } catch (Exception ex) {
            Log.e("WordServiceImpl", ex.getMessage(), ex);
        }
    }

    public String importFileInDb(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        wordRepository.clearWordTable();
        try (FileInputStream fileInputStream = context.openFileInput(filePath);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            String line = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] wordAtr = line.split(",", 10);
                Word word = null;

                String wordOriginal = wordAtr[1];
                String wordTranslated = wordAtr[2];
                Instant dateCreated = Instant.ofEpochMilli(Long.parseLong(wordAtr[3]));
                Instant dateUpdated = Instant.ofEpochMilli(Long.parseLong(wordAtr[4]));
                Instant dateShowed = null;
                long dateShowedLong = Long.parseLong(wordAtr[5]);
                if (dateShowedLong > 0) {
                    dateShowed = Instant.ofEpochMilli(dateShowedLong);
                }
                long addCounter = Long.parseLong(wordAtr[6]);
                long correctCheckCounter = Long.parseLong(wordAtr[7]);
                long incorrectCheckCounter = Long.parseLong(wordAtr[8]);
                long rating = Long.parseLong(wordAtr[9]);
                word = new Word(wordOriginal, wordTranslated, dateCreated, dateUpdated, dateShowed,
                        addCounter, correctCheckCounter, incorrectCheckCounter, rating);
                if (word != null) {
                    wordRepository.insertWord(word);
                }
            }
        } catch (Exception ex) {
            Log.e("WordServiceImpl", ex.getMessage(), ex);
        }
        return filePath;
    }
 */
}
