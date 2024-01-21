package ru.alimov.repeatenglish.fragment;

import static ru.alimov.repeatenglish.util.Const.WORD_CARD_ANSWER;
import static ru.alimov.repeatenglish.util.Const.WORD_CARD_QUESTION;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.alimov.repeatenglish.R;
import ru.alimov.repeatenglish.service.WordService;
import ru.alimov.repeatenglish.service.WordServiceImpl;

/**
 * Dialog window with translation.
 */
public class CheckingDialog extends DialogFragment {

    private String question;
    private String answer;

    private static WordService wordService;


    public static CheckingDialog newInstance(String question, String answer) {
        CheckingDialog checkingDialog = new CheckingDialog();

        Bundle args = new Bundle();
        args.putString(WORD_CARD_QUESTION, question);
        args.putString(WORD_CARD_ANSWER, answer);
        checkingDialog.setArguments(args);
        return checkingDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordService = new WordServiceImpl(getContext());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.question = getArguments().getString(WORD_CARD_QUESTION);
        this.answer = getArguments().getString(WORD_CARD_ANSWER);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_checking_dialog, null);

        //Show translation of selected word.
        TextView word_card_question = dialogView.findViewById(R.id.word_answer);
        word_card_question.setText(answer);

        //Set handler for positive button.
        Button btnCorrectAnswer = dialogView.findViewById(R.id.btn_correct_answer);
        btnCorrectAnswer.setOnClickListener(view -> {
                    if (wordService.incrementCorrectCheckCounter(question)) {
                        this.dismiss();
                    }
                }
        );

        //Set handler for negative button.
        Button btnIncorrectAnswer = dialogView.findViewById(R.id.btn_incorrect_answer);
        btnIncorrectAnswer.setOnClickListener(view -> {
                    if (wordService.incrementIncorrectCheckCounter(question)) {
                        this.dismiss();
                    }
                }
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Rate yourself!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(dialogView)
                .create();
    }

}