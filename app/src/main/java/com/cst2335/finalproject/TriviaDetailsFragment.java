package com.cst2335.finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * TriviaDetailsFragment is a piece of an application's user interface or behavior that can be placed in an Activity.
 * @author Juan Ni
 */
public class TriviaDetailsFragment extends Fragment {
    private Bundle dataFromResultActivity;
    private String playerName;
    private int score;
    private int questionAmount;
    private String difficultyType;
    private String questionType;
    private AppCompatActivity parentTriviaActivity;
    private TextView playerNameView, scoreView, amountView, difficultyView, questionTypeView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromResultActivity = getArguments();
        playerName = dataFromResultActivity.getString(TriviaGameResultActivity.PLAYER_NAME);
        score = dataFromResultActivity.getInt(TriviaGameResultActivity.PLAYER_SCORE);
        questionAmount = dataFromResultActivity.getInt(TriviaGameResultActivity.QUESTION_AMOUNT);
        difficultyType = dataFromResultActivity.getString(TriviaGameResultActivity.DIFFICULTY_TYPE);
        questionType = dataFromResultActivity.getString(TriviaGameResultActivity.QUESTION_TYPE);

        // Inflate the layout for this fragment
        View triviaResult =  inflater.inflate(R.layout.fragment_trivia_details, container, false);

        playerNameView = triviaResult.findViewById(R.id.playerNameFragment);
        scoreView = triviaResult.findViewById(R.id.playerScoreFragment);
        amountView = triviaResult.findViewById(R.id.questionAmountFragment);
        difficultyView = triviaResult.findViewById(R.id.difficultyTypeFragment);
        questionTypeView = triviaResult.findViewById(R.id.questionTypeFragment);

        playerNameView.setText(getResources().getString(R.string.showPlayerName) + " " + playerName);
        scoreView.setText(getResources().getString(R.string.showScore) + " " + score);
        amountView.setText(getResources().getString(R.string.showQuestionAmount) + " " + questionAmount + " " + getResources().getString(R.string.questionText));
        difficultyView.setText(getResources().getString(R.string.showDifficultyType) + " " + difficultyType);
        questionTypeView.setText(getResources().getString(R.string.showQuestionType) + " " + questionType);




        // get the delete button, and add a click listener:
        Button hideButton = triviaResult.findViewById(R.id.hideTriviaFragBtn);

        hideButton.setOnClickListener( clk -> {
            //Tell the parent activity to remove
            parentTriviaActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        return triviaResult;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentTriviaActivity = (AppCompatActivity)context;
    }
}