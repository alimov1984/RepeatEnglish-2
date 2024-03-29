package ru.alimov.repeatenglish.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import ru.alimov.repeatenglish.adapter.WordCardDeckAdapter;
import ru.alimov.repeatenglish.model.Word;
import ru.alimov.repeatenglish.service.ServiceSupplier;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.R;

/**
 * Checking page.
 */
public class CheckActivity extends AppCompatActivity {

    private WordService wordService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wordService = ServiceSupplier.getWordService(getApplicationContext());
        setContentView(R.layout.activity_check);

        List<Word> wordList = wordService.getWordsForChecking();

        ViewPager2 word_pager = findViewById(R.id.word_pager);
        FragmentStateAdapter pageAdapter = new WordCardDeckAdapter(this, wordList);
        word_pager.setAdapter(pageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, word_pager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(TabLayout.Tab tab, int position) {
                        tab.setText("Word " + (position + 1));
                    }
                });
        tabLayoutMediator.attach();

        androidx.appcompat.widget.Toolbar myToolbar =
                (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        TextView msgLabel = findViewById(R.id.msgLabel);
        if (wordList.size() == 0) {
            msgLabel.setText(getResources().getString(R.string.checking_words_empty));
            msgLabel.setVisibility(View.VISIBLE);
            word_pager.setVisibility(View.INVISIBLE);
            return;
        } else {
            msgLabel.setVisibility(View.INVISIBLE);
            word_pager.setVisibility(View.VISIBLE);
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
            case R.id.menu_word_add:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}