package ru.alimov.repeatenglish.workers;


import static android.content.Context.MODE_PRIVATE;

import static ru.alimov.repeatenglish.util.Const.EXPORT_DB_FILE_PATH;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.List;

import ru.alimov.repeatenglish.R;
import ru.alimov.repeatenglish.model.Word;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.util.WorkerUtils;

public class ExportDbWorker extends Worker {

    private static final String TAG = ExportDbWorker.class.getSimpleName();
    private final WordService wordService;

    public ExportDbWorker(Context context, WorkerParameters workerParams, WordService wordService) {
        super(context, workerParams);
        this.wordService = wordService;
    }

    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        List<Word> wordList = wordService.getWordsForExport();
        String fileName = String.format("repeatEnglish_{%s}_{%s}_{%s}_{%s}_{%s}_{%s}.csv"
                , LocalDateTime.now().getDayOfMonth()
                , LocalDateTime.now().getMonthValue()
                , LocalDateTime.now().getYear()
                , LocalDateTime.now().getHour()
                , LocalDateTime.now().getMinute()
                , LocalDateTime.now().getSecond());

        String filePath = "export/" + fileName;
        try (FileOutputStream fileOutputStream = context.openFileOutput(filePath, MODE_PRIVATE);
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

            String notifyMessage = String.format("Данные успешно экспортированы в файл %s", filePath);
            WorkerUtils.makeStatusNotification(
                    context.getString(R.string.app_name),
                    notifyMessage,
                    context);

            Data outputData = new Data.Builder()
                    .putString(EXPORT_DB_FILE_PATH, filePath)
                    .build();
            return Worker.Result.success(outputData);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return Worker.Result.failure();
        }
    }
}
