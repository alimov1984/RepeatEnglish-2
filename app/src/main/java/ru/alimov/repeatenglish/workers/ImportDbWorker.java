package ru.alimov.repeatenglish.workers;

import static ru.alimov.repeatenglish.util.Const.IMPORT_DB_FILE_PATH;

import android.content.Context;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.Instant;

import ru.alimov.repeatenglish.model.Word;
import ru.alimov.repeatenglish.service.WordService;

public class ImportDbWorker extends Worker {
    private static final String TAG = ExportDbWorker.class.getSimpleName();
    private final WordService wordService;

    public ImportDbWorker(Context context, WorkerParameters workerParams, WordService wordService) {
        super(context, workerParams);
        this.wordService = wordService;
    }

    @Override
    public Result doWork() {
        String filePath = getInputData().getString(IMPORT_DB_FILE_PATH);
        File file = new File(filePath);
        if (!file.exists()) {
            return Worker.Result.failure();
        }
        wordService.clearWordTable();
        Context context = getApplicationContext();
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
                    wordService.insertWord(word);
                }
            }
            return Result.success();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return Result.failure();
        }
    }
}
