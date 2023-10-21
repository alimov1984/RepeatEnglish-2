package ru.alimov.repeatenglish.activity;

import static ru.alimov.repeatenglish.util.Const.PREFERENCE_NAME;
import static ru.alimov.repeatenglish.util.Const.SETTING_WORD_CHECKING_COUNT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ru.alimov.repeatenglish.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText wordCountEdit = findViewById(R.id.wordCountEdit);

        settings = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        Integer currentWordCount = settings.getInt(SETTING_WORD_CHECKING_COUNT, 10);
        wordCountEdit.setText(currentWordCount.toString());
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
                //Toast.makeText(this, "menu_word_check", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}