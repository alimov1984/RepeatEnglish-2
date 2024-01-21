package ru.alimov.repeatenglish.workers;


import static ru.alimov.repeatenglish.util.Const.EXPORT_DB_FILE_PATH;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.List;

import ru.alimov.repeatenglish.R;
import ru.alimov.repeatenglish.model.Word;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.service.WordServiceImpl;
import ru.alimov.repeatenglish.util.WorkerUtils;

/**
 * Worker is used to export db to file.
 */
public class ExportDbWorker extends Worker {

    private static final String TAG = ExportDbWorker.class.getSimpleName();
    private final WordService wordService;

    public ExportDbWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        this.wordService = new WordServiceImpl(context);
    }

    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        List<Word> wordList = wordService.getWordsForExport();
        String fileName = String.format("repeatEnglish_%s_%s_%s_%s_%s_%s.csv"
                , LocalDateTime.now().getDayOfMonth()
                , LocalDateTime.now().getMonthValue()
                , LocalDateTime.now().getYear()
                , LocalDateTime.now().getHour()
                , LocalDateTime.now().getMinute()
                , LocalDateTime.now().getSecond());

        String path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath();
        String filePath = path + "/" + fileName;
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try
        {
            fileOutputStream = new FileOutputStream(filePath);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write("Id,WordOriginal,WordTranslated,DateCreated,DateUpdated,DateShowed,AddCounter,CorrectCheckCounter,IncorrectCheckCounter,Rating\n");
            for (Word word : wordList) {
                outputStreamWriter.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n"
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
            outputStreamWriter.close();
            String notifyMessage = String.format("Данные успешно экспортированы в файл %s",
                    filePath);
            WorkerUtils.makeStatusNotification(
                    context.getResources().getString(R.string.app_name),
                    notifyMessage,
                    context);

            Data outputData = new Data.Builder()
                    .putString(EXPORT_DB_FILE_PATH, filePath)
                    .build();
            fileOutputStream.close();
            return Worker.Result.success(outputData);

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return Worker.Result.failure();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (IOException ex2) {
                Log.e(TAG, ex2.getMessage(), ex2);
            }

        }
    }
}
