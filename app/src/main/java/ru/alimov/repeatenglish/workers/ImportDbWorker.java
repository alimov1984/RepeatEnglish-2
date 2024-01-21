package ru.alimov.repeatenglish.workers;

import static ru.alimov.repeatenglish.util.Const.IMPORT_DB_FILE_PATH;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

import ru.alimov.repeatenglish.R;
import ru.alimov.repeatenglish.model.Word;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.service.WordServiceImpl;
import ru.alimov.repeatenglish.util.WorkerUtils;

/**
 * Worker is used for import from file to db.
 */
public class ImportDbWorker extends Worker {
    private static final String TAG = ExportDbWorker.class.getSimpleName();
    private final WordService wordService;

    public ImportDbWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        this.wordService = new WordServiceImpl(context);
    }

    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String filePath = getInputData().getString(IMPORT_DB_FILE_PATH);

        final String[] split = filePath.split(":");
        File externalStorage = Environment.getExternalStorageDirectory();
        filePath = externalStorage.getPath() + "/" + split[1];

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            wordService.clearWordTable();
            //Read caption line from the file.
            String line = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] wordAtr = line.split(",", 10);
                Word word = null;
                String wordOriginal = wordAtr[1];
                String wordTranslated = wordAtr[2];
                Instant dateCreated = Instant.parse(wordAtr[3]);
                Instant dateUpdated = Instant.parse(wordAtr[4]);
                Instant dateShowed = null;
                if (!wordAtr[5].equals("0")) {
                    dateShowed = Instant.parse(wordAtr[5]);
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
            inputStreamReader.close();
            fileInputStream.close();
            String notifyMessage = String.format("Данные успешно импортированы из файла %s",
                    filePath);
            WorkerUtils.makeStatusNotification(
                    context.getResources().getString(R.string.app_name),
                    notifyMessage,
                    context);

            return Result.success();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return Result.failure();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (IOException ex2) {
                Log.e(TAG, ex2.getMessage(), ex2);
            }
        }
    }
}
