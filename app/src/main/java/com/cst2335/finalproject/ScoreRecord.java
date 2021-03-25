package com.cst2335.finalproject;

/**
 * ScoreRecord class is a blueprint of saved score records of trivia game.
 * @author Juan Ni
 */
public class ScoreRecord {

    private String name;
    private int score;
    private String difficultyType;
    private String questionType;
    private int questionAmount;
    private long id;


    /**
     * ScoreRecord constructor constructs a score record with a palyer name, score, difficulty type, question type, question number and id.
     * @param name String value of player name
     * @param score int value of score
     * @param difficultyType String value of question difficulty type
     * @param questionType String value of question type
     * @param questionNumber int value of total number of questions
     * @param id int value of score record's database id
     */
    public ScoreRecord(String name, int score, String difficultyType, String questionType, int questionNumber, long id) {
        this.name = name;
        this.score = score;
        this.difficultyType = difficultyType;
        this.questionType = questionType;
        this.questionAmount = questionNumber;
        this.id = id;
    }

    /**
     * Gets player name.
     * @return String value of player name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets player score.
     * @return int value of player score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets question difficulty type.
     * @return String value of question difficulty type.
     */
    public String getDifficultyType() {
        return difficultyType;
    }

    /**
     * Gets question type.
     * @return String value of question type.
     */
    public String getQuestionType() {
        return questionType;
    }

    /**
     * Gets total number of questions.
     * @return int value of total number of questions.
     */
    public int getQuestionAmount() {
        return questionAmount;
    }

    /**
     * Gets score record's database id.
     * @return int value of score record's database id.
     */
    public long getId() {
        return id;
    }
}
