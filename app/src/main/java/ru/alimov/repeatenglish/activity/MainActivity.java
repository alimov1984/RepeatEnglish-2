package ru.alimov.repeatenglish.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ru.alimov.repeatenglish.R;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.service.WordServiceImpl;

public class MainActivity extends AppCompatActivity {

    private WordService wordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordService = new WordServiceImpl(this);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        EditText wordOriginalEdit = findViewById(R.id.wordOriginalEdit);
        EditText wordTranslatedEdit = findViewById(R.id.wordTranslatedEdit);

        String wordOriginal = wordOriginalEdit.getText().toString().trim();
        if (wordOriginal.length() == 0) {
            Toast.makeText(this, "Необходимо заполнить поле 'Новое слово'", Toast.LENGTH_SHORT).show();
            return;
        }

        String wordTranslated = wordTranslatedEdit.getText().toString().trim();
        if (wordTranslated.length() == 0) {
            Toast.makeText(this, "Необходимо заполнить поле 'Перевод'", Toast.LENGTH_SHORT).show();
            return;
        }

        String result = wordService.insertWord(wordOriginal, wordTranslated);
        if (result != null && result.length() > 0) {
            wordOriginalEdit.setText("");
            wordTranslatedEdit.setText("");
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
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
            case R.id.menu_word_check:
                Intent intent = new Intent(this, CheckActivity.class);
                startActivity(intent);
                //Toast.makeText(this, "menu_word_check", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_settings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
                //Toast.makeText(this, "my_settings", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}