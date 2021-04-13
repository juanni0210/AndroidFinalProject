package com.cst2335.finalproject;

import android.content.res.Resources;

/**
 * QuestionType Enum that represents the question's difficulty type
 * @author Juan Ni
 */
public enum QuestionType {
    Any, Multiple, TrueOrFalse;

    /**
     * Static method to display question type text.
     *  @param type Difficulty type
     *  @param res Resources type
     * @return String value of question type.
     */
    public static String toDisplayText(QuestionType type, Resources res) {
        String[] texts = res.getStringArray(R.array.question_type_array);
        return texts[type.ordinal()];
    }

    /**
     * Static method to convert text to Enum.
     *  @param type String type
     *  @param res Resources type
     * @return Question type enum.
     */
    public static QuestionType toEnum(String type, Resources res) {
        String[] texts = res.getStringArray(R.array.question_type_array);
        if (type.equals(texts[Any.ordinal()]))
            return Any;
        else if (type.equals(texts[Multiple.ordinal()]))
            return Multiple;
        else if (type.equals(texts[TrueOrFalse.ordinal()]))
            return TrueOrFalse;

        throw new RuntimeException("Invalid Question Type");
    }

    /**
     * Static method to convert Enum and place to Url.
     *  @param type String type
     * @return String value of question type.
     */
    public static String toUrlString(QuestionType type) {
        switch (type) {
            case Any:
                return "anytype";
            case Multiple:
                return "multiple";
            case TrueOrFalse:
                return "boolean";
            default:
                throw new RuntimeException("Invalid Question Type");
        }
    }
}
