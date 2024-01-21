package ru.alimov.repeatenglish.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import ru.alimov.repeatenglish.fragment.WordCardFragment;
import ru.alimov.repeatenglish.model.Word;

/**
 * Adapter store words for view pager.
 */
public class WordCardDeckAdapter extends FragmentStateAdapter {
    private List<Word> wordList;

    public WordCardDeckAdapter(FragmentActivity fragmentActivity, List<Word> wordList) {
        super(fragmentActivity);
        this.wordList = wordList;
    }

    @Override
    public Fragment createFragment(int position) {
        Word word = wordList.get(position);
        return (WordCardFragment.newInstance(word.getWordOriginal(), word.getWordTranslated()));
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }
}
