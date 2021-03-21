package com.cst2335.finalproject;

public class ScoreRecord {

    private String name;
    private int score;
    private String difficultyType;
    private String questionType;
    private int questionAmount;
    private long id;


    public ScoreRecord(String name, int score, String difficultyType, String questionType, int questionNumber, long id) {
        this.name = name;
        this.score = score;
        this.difficultyType = difficultyType;
        this.questionType = questionType;
        this.questionAmount = questionNumber;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getDifficultyType() {
        return difficultyType;
    }

    public String getQuestionType() {
        return questionType;
    }
    public int getQuestionAmount() {
        return questionAmount;
    }

    public long getId() {
        return id;
    }
}
