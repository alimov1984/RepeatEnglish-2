package ru.alimov.repeatenglish.activity;

import static ru.alimov.repeatenglish.util.Const.EXPORT_DB_OUTPUT;
import static ru.alimov.repeatenglish.util.Const.IMPORT_DB_FILE_PATH;
import static ru.alimov.repeatenglish.util.Const.IMPORT_DB_OUTPUT;
import static ru.alimov.repeatenglish.util.Const.PREFERENCE_NAME;
import static ru.alimov.repeatenglish.util.Const.SETTING_WORD_CHECKING_COUNT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ru.alimov.repeatenglish.R;
import ru.alimov.repeatenglish.workers.ExportDbWorker;
import ru.alimov.repeatenglish.workers.ImportDbWorker;

/**
 * Setting page.
 */
public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences settings;

    private WorkManager workManager;

    private ActivityResultLauncher<String> dbImportActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        workManager = WorkManager.getInstance(getApplicationContext());

        EditText wordCountEdit = findViewById(R.id.wordCountEdit);

        settings = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        //Get value of current word count for checking page.
        Integer currentWordCount = settings.getInt(SETTING_WORD_CHECKING_COUNT, 10);
        wordCountEdit.setText(currentWordCount.toString());

        registerHandlerForFilePicker();

        androidx.appcompat.widget.Toolbar myToolbar =
                (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    public void saveSettings(View view) {
        EditText wordCountEdit = findViewById(R.id.wordCountEdit);
        String wordCountStr = wordCountEdit.getText().toString().trim();
        if (wordCountStr.length() == 0) {
            Toast.makeText(this,
                    "Необходимо ввести значение в поле 'Количество слов при проверке(1-100)'",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int wordCount = Integer.parseInt(wordCountStr);
        if (wordCount <= 0 || wordCount > 100) {
            Toast.makeText(this,
                    "Необходимо ввести корректное значение в поле 'Количество слов при проверке(1-100)'",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putInt(SETTING_WORD_CHECKING_COUNT, wordCount);
        settingsEditor.apply();
        Toast.makeText(this,
                "Настройки успешно сохранены",
                Toast.LENGTH_SHORT).show();
    }

    private void registerHandlerForFilePicker() {
        dbImportActivity = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result ->
                {
                    if (result == null || result.getPath() == null) {
                        Toast.makeText(this,
                                "File isn't selected",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Constraints constraints = new Constraints.Builder()
                            .setRequiresStorageNotLow(true)
                            .build();

                    Data inputData = new Data.Builder()
                            .putString(IMPORT_DB_FILE_PATH, result.getPath())
                            .build();

                    //Execute import in separated thread.
                    WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(ImportDbWorker.class)
                            .setConstraints(constraints)
                            .addTag(IMPORT_DB_OUTPUT)
                            .setInputData(inputData)
                            .build();

                    workManager.enqueue(myWorkRequest).getResult();
                });
    }

    public void onExportBtnClick(View view) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build();
        //Execute export db in separated thread.
        WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(ExportDbWorker.class)
                .setConstraints(constraints)
                .addTag(EXPORT_DB_OUTPUT)
                .build();

        workManager.enqueue(myWorkRequest).getResult();
    }

    public void onImportBtnClick(View view) {
        dbImportActivity.launch("*/*");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        switch (menuItemId) {
            case R.id.menu_word_add:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_word_check:
                Intent intent2 = new Intent(this, CheckActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}