package ru.alimov.repeatenglish.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import ru.alimov.repeatenglish.R;
import ru.alimov.repeatenglish.model.MainActivityUiState;
import ru.alimov.repeatenglish.model.MainActivityViewModel;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.service.WordServiceImpl;

public class MainActivity extends AppCompatActivity {

    private WordService wordService;

    private MainActivityViewModel uiModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> true);
        super.onCreate(savedInstanceState);
        wordService = new WordServiceImpl(this);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            splashScreen.setKeepOnScreenCondition(() -> false);
            mainActivityProcess();
        }, 1000);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        EditText wordOriginalEdit = findViewById(R.id.wordOriginalEdit);
        EditText wordTranslatedEdit = findViewById(R.id.wordTranslatedEdit);

        MainActivityUiState uiState = new MainActivityUiState(wordOriginalEdit.getText().toString(),
                wordTranslatedEdit.getText().toString());
        uiModel.getUiState().setValue(uiState);
    }

    private void mainActivityProcess() {
        setContentView(R.layout.activity_main);
        androidx.appcompat.widget.Toolbar myToolbar =
                (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        EditText wordOriginalEdit = findViewById(R.id.wordOriginalEdit);
        EditText wordTranslatedEdit = findViewById(R.id.wordTranslatedEdit);

        uiModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        final Observer<MainActivityUiState> uiObserver = new Observer<MainActivityUiState>() {
            @Override
            public void onChanged(@Nullable final MainActivityUiState mainActivityUiState) {
                // Update the UI, in this case, a TextView.
                wordOriginalEdit.setText(mainActivityUiState.getWordOriginate());
                wordTranslatedEdit.setText(mainActivityUiState.getWordTranslated());
            }
        };
        uiModel.getUiState().observe(this, uiObserver);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });

    }

    public void sendMessage(View view) {
        EditText wordOriginalEdit = findViewById(R.id.wordOriginalEdit);
        EditText wordTranslatedEdit = findViewById(R.id.wordTranslatedEdit);

        MainActivityUiState uiState = new MainActivityUiState(wordOriginalEdit.getText().toString(),
                wordTranslatedEdit.getText().toString());
        uiModel.getUiState().setValue(uiState);

        String wordOriginal = uiState.getWordOriginate().trim();
        if (wordOriginal.length() == 0) {
            Toast.makeText(this, "Необходимо заполнить поле 'Новое слово'", Toast.LENGTH_SHORT).show();
            return;
        }

        String wordTranslated = uiState.getWordTranslated().trim();
        if (wordTranslated.length() == 0) {
            Toast.makeText(this, "Необходимо заполнить поле 'Перевод'", Toast.LENGTH_SHORT).show();
            return;
        }

        String result = wordService.insertWord(wordOriginal, wordTranslated);
        if (result != null && result.length() > 0) {
            uiState = new MainActivityUiState("", "");
            uiModel.getUiState().setValue(uiState);
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