package ru.alimov.repeatenglish.activity.setting;

import androidx.preference.PreferenceFragmentCompat;
import android.os.Bundle;

import ru.alimov.repeatenglish.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }
}
