package ru.alimov.repeatenglish.activity.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.alimov.repeatenglish.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }
}