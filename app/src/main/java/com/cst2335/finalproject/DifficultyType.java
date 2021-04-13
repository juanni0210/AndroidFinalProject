package com.cst2335.finalproject;

import android.content.res.Resources;

/**
 * DifficultyType Enum that represents the question's difficulty type
 * @author Juan Ni
 */
public enum DifficultyType {

    Easy, Medium, Hard;

    /**
     * Static method to display difficulty type text.
     *  @param type Difficulty type
     *  @param res Resources type
     * @return String value of difficulty type.
     */
    public static String toDisplayText(DifficultyType type, Resources res) {
        String[] texts = res.getStringArray(R.array.difficulty_array);
        return texts[type.ordinal()];
    }

    /**
     * Static method to convert text to Enum.
     *  @param type String type
     *  @param res Resources type
     * @return Difficulty type enum.
     */
    public static DifficultyType toEnum(String type, Resources res) {
        String[] texts = res.getStringArray(R.array.difficulty_array);
        if (type.equals(texts[Easy.ordinal()]))
            return Easy;
        else if (type.equals(texts[Medium.ordinal()]))
            return Medium;
        else if (type.equals(texts[Hard.ordinal()]))
            return Hard;

        throw new RuntimeException("Invalid Difficulty Type");
    }

    /**
     * Static method to convert Enum and place to Url.
     *  @param type String type
     * @return String value of difficulty type.
     */
    public static String toUrlString(DifficultyType type) {
        switch (type) {
            case Easy:
                return "easy";
            case Medium:
                return "medium";
            case Hard:
                return "hard";
            default:
                throw new RuntimeException("Invalid Difficulty Type");
        }
    }
}
