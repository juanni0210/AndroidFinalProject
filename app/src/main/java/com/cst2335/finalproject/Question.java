package com.cst2335.finalproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Question class is a blueprint of questions of trivia game.
 * @author Juan Ni
 */
public class Question {
    private String questionType;
    private String difficultyType;
    private String questionText;
    private String correctAns;
    private ArrayList<String> choices = new ArrayList<>();


    /**
     * Question construtor constructs a question with a JSONObject object
     * @param jsonObj JSONObject type
     * @throws JSONException - if problems occur when using JSON API
     */
    public Question(JSONObject jsonObj) throws JSONException {
        this.questionType = jsonObj.getString("type");
        this.difficultyType = jsonObj.getString("difficulty");
        this.questionText = jsonObj.getString("question");
        this.correctAns = jsonObj.getString("correct_answer");
        choices.add(correctAns);
        JSONArray jsonArray = jsonObj.getJSONArray("incorrect_answers");
        if (jsonArray != null) {
            for(int i = 0; i < jsonArray.length(); i++) {
                choices.add(jsonArray.getString(i));
            }
        }
        Collections.shuffle(choices);
    }

    /**
     * Gets question type.
     * @return String value of question type.
     */
    public String getQuestionType() { return questionType; }

    /**
     * Gets question difficulty type.
     * @return String value of question difficulty type.
     */
    public String getDifficultyType() { return difficultyType; }

    /**
     * Gets question text.
     * @return String value of question text.
     */
    public String getQuestionText() { return questionText; }

    /**
     * Gets correct answer.
     * @return String value of correct answer.
     */
    public String getCorrectAns() {
        return correctAns;
    }

    /**
     * Gets a string list of choices including correct answer and incorrect answer.
     * @return String of ArrayList of all choices.
     */
    public ArrayList<String> getChoices() {
        return choices;
    }

}
