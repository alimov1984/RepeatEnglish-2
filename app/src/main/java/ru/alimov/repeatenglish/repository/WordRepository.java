package ru.alimov.repeatenglish.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ru.alimov.repeatenglish.model.Word;

public class WordRepository {
    private final static String DICTIONARY_TABLE_NAME = "word_dictionary";
    private final static String PRIMARY_COLUMN_ID = "id";
    private Database database;

    public WordRepository(Context context) {
        database = new Database(context);
    }

    private Word wordConstructor(Cursor dbCursor) {
        long id = dbCursor.getLong(0);
        String word_original = dbCursor.getString(1);
        String word_translated = dbCursor.getString(2);
        Instant.ofEpochMilli(dbCursor.getLong(3));
        Instant dateCreated = Instant.now();
        Instant dateUpdated = Instant.ofEpochMilli(dbCursor.getLong(4));
        Instant dateShowed = null;
        if (!dbCursor.isNull(5)) {
            dateShowed = Instant.ofEpochMilli(dbCursor.getLong(5));
        }
        long addCounter = dbCursor.getLong(6);
        long correctCheckCounter = dbCursor.getLong(7);
        long incorrectCheckCounter = dbCursor.getLong(8);
        long rating = dbCursor.getLong(9);

        Word word = new Word(id, word_original, word_translated, dateCreated, dateUpdated, dateShowed,
                addCounter, correctCheckCounter, incorrectCheckCounter, rating);
        return word;
    }

    public void clearWordTable() {
        database.deleteAllRows(DICTIONARY_TABLE_NAME);
    }

    public Word getWordById(int id) {
        String sql = "SELECT id, word_original, word_translated, dateCreated, dateUpdated,"
                + "dateShowed, add_counter, correct_check_counter, incorrect_check_counter, rating"
                + "FROM [word_dictionary] WHERE rowid = ?";
        Word word = database.getObject(sql, this::wordConstructor, new String[]{String.valueOf(id)});
        return word;
    }

    public Word getWordByOriginal(String wordOriginal) {
        String sql = "SELECT id, word_original, word_translated, dateCreated, dateUpdated,"
                + "dateShowed, add_counter, correct_check_counter, incorrect_check_counter, rating"
                + " FROM [word_dictionary] WHERE word_original = ?";
        Word word = database.getObject(sql, this::wordConstructor, new String[]{wordOriginal});
        return word;
    }

    public Optional<Long> insertWord(Word word) {
        ContentValues cv = new ContentValues();
        cv.put("word_original", word.getWordOriginal().replace(",",""));
        cv.put("word_translated", word.getWordTranslated().replace(",",""));
        cv.put("dateCreated", word.getDateCreated().toEpochMilli());
        cv.put("dateUpdated", word.getDateUpdated().toEpochMilli());
        cv.putNull("dateShowed");
        cv.put("add_counter", word.getAddCounter());
        cv.put("correct_check_counter", word.getCorrectCheckCounter());
        cv.put("incorrect_check_counter", word.getIncorrectCheckCounter());
        cv.put("rating", word.getRating());

        Optional<Long> insertedRowId = database.insertRow(DICTIONARY_TABLE_NAME, cv);
        return insertedRowId;
    }

    public Optional<Integer> updateWord(Word word) {
        ContentValues cv = new ContentValues();
        cv.put("word_original", word.getWordOriginal().replace(",",""));
        cv.put("word_translated", word.getWordTranslated().replace(",",""));
        cv.put("dateUpdated", word.getDateUpdated().toEpochMilli());
        cv.put("add_counter", word.getAddCounter());
        cv.put("correct_check_counter", word.getCorrectCheckCounter());
        cv.put("incorrect_check_counter", word.getIncorrectCheckCounter());
        cv.put("rating", word.getRating());

        Optional<Integer> afecctedRows = database.updateRow(DICTIONARY_TABLE_NAME,
                PRIMARY_COLUMN_ID, word.getId(), cv);
        return afecctedRows;
    }

    public List<Word> getWordsForChecking(int wordCount)
    {
        String sql = "SELECT id, word_original, word_translated, dateCreated, dateUpdated, dateShowed,\n" +
                "                add_counter, correct_check_counter, incorrect_check_counter, rating\n" +
                "                FROM [word_dictionary]   \n" +
                "                WHERE dateShowed IS NULL OR (? - dateShowed) > ?\n" +
                "                ORDER BY rating DESC\n" +
                "                LIMIT ?";
        String[] params = new String[]{
                String.valueOf(Instant.now().toEpochMilli()),
                String.valueOf(TimeUnit.DAYS.toMillis(1)),
                String.valueOf(wordCount)};

        List<Word> wordList = database.getObjectList(sql, this::wordConstructor, params);
        return wordList;
    }

    public Optional<Integer> incrementWordCorrectCheckCounter(Word word)
    {
        ContentValues cv = new ContentValues();
        cv.put("correct_check_counter", word.getCorrectCheckCounter());
        cv.put("rating", word.getRating());
        Optional<Integer> affectedRows = database.updateRow(DICTIONARY_TABLE_NAME,
                PRIMARY_COLUMN_ID, word.getId(), cv);
        return affectedRows;
    }

    public Optional<Integer> incrementWordIncorrectCheckCounter(Word word)
    {
        ContentValues cv = new ContentValues();
        cv.put("incorrect_check_counter", word.getIncorrectCheckCounter());
        cv.put("rating", word.getRating());
        Optional<Integer> affectedRows = database.updateRow(DICTIONARY_TABLE_NAME,
                PRIMARY_COLUMN_ID, word.getId(), cv);
        return affectedRows;
    }

    public Optional<Integer> updateWordDateShowed(Word word)
    {
        ContentValues cv = new ContentValues();
        cv.put("dateShowed", Instant.now().toEpochMilli());
        Optional<Integer> affectedRows = database.updateRow(DICTIONARY_TABLE_NAME,
                PRIMARY_COLUMN_ID, word.getId(), cv);
        return affectedRows;
    }

    public List<Word> getWordsForExport(int wordCount)
    {
        String sql = "SELECT id, word_original, word_translated, dateCreated, dateUpdated, dateShowed,\n" +
                "                add_counter, correct_check_counter, incorrect_check_counter, rating\n" +
                "                FROM [word_dictionary]   \n" +
                "                ORDER BY rowid";

        List<Word> wordList = database.getObjectList(sql, this::wordConstructor, null);
        return wordList;
    }

}
