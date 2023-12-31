package ru.alimov.repeatenglish.fragment;

import static ru.alimov.repeatenglish.util.Const.WORD_CARD_ANSWER;
import static ru.alimov.repeatenglish.util.Const.WORD_CARD_QUESTION;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.alimov.repeatenglish.R;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.service.WordServiceImpl;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WordCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordCardFragment extends Fragment {

    private String question;
    private String answer;

    private WordService wordService;

    public WordCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param question Parameter 1.
     * @param answer   Parameter 2.
     * @return A new instance of fragment WordCardFragment.
     */
    public static WordCardFragment newInstance(String question, String answer) {
        WordCardFragment fragment = new WordCardFragment();
        Bundle args = new Bundle();
        args.putString(WORD_CARD_QUESTION, question);
        args.putString(WORD_CARD_ANSWER, answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordService = new WordServiceImpl(this.getContext());
        if (getArguments() != null) {
            Bundle args = getArguments();
            this.question = args.getString(WORD_CARD_QUESTION, "");
            this.answer = args.getString(WORD_CARD_ANSWER, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("WordCardFragment", "onCreateView");
        View resultView = inflater.inflate(R.layout.fragment_word_card, container, false);
        TextView word_card_question = resultView.findViewById(R.id.word_card_question);
        word_card_question.setText(this.question);
        return resultView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("WordCardFragment", "onViewCreated");
        TextView word_card_question = view.findViewById(R.id.word_card_question);
        word_card_question.setText(this.question);

        word_card_question.setOnClickListener(view2 -> {
            CheckingDialog checkingDialog = CheckingDialog.newInstance(question, answer);
            checkingDialog.show(getActivity().getSupportFragmentManager(), "checkingDialog");
        });
        //wordService.updateDateShowed(this.question);
    }
}