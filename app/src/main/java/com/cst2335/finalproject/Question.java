package com.cst2335.finalproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Question {
    private String questionType;
    private String difficultyType;
    private String questionText;
    private String correctedAns;
    private ArrayList<String> choices = new ArrayList<>();



    public Question(JSONObject jsonObj) throws JSONException {
        this.questionType = jsonObj.getString("type");
        this.difficultyType = jsonObj.getString("difficulty");
        this.questionText = jsonObj.getString("question");
        this.correctedAns = jsonObj.getString("correct_answer");
        choices.add(correctedAns);
        JSONArray jsonArray = jsonObj.getJSONArray("incorrect_answers");
        if (jsonArray != null) {
            for(int i = 0; i < jsonArray.length(); i++) {
                choices.add(jsonArray.getString(i));
            }
        }
        Collections.shuffle(choices);
    }



    public String getQuestionType() { return questionType; }

    public String getDifficultyType() { return difficultyType; }

    public String getQuestionText() { return questionText; }

    public String getCorrectedAns() {
        return correctedAns;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public void setDifficultyType(String difficultyType) { this.difficultyType = difficultyType; }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setCorrectedAns(String correctedAns) {
        this.correctedAns = correctedAns;
    }

    public void setIncorrectAns(ArrayList<String> choices) { this.choices = choices; }

    public boolean isAnsweredCorrectly(String selectedAnswer) {
        return selectedAnswer == correctedAns;
    }
}
