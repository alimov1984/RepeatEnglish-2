package ru.alimov.repeatenglish.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import ru.alimov.repeatenglish.adapter.WordCardDeckAdapter;
import ru.alimov.repeatenglish.model.Word;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.service.WordServiceImpl;
import ru.alimov.repeatenglish.R;

public class CheckActivity extends AppCompatActivity {

    private WordService wordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        wordService = new WordServiceImpl(this);
        List<Word> wordList = wordService.getWordsForChecking();

        ViewPager2 word_pager = findViewById(R.id.word_pager);
        FragmentStateAdapter pageAdapter = new WordCardDeckAdapter(this, wordList);
        word_pager.setAdapter(pageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, word_pager,
                (tab, position) -> tab.setText((position + 1)));
        tabLayoutMediator.attach();
    }
}