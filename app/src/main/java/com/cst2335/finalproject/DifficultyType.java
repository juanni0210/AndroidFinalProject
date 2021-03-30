package com.cst2335.finalproject;

import android.content.res.Resources;

public enum DifficultyType {

    Easy, Medium, Hard;

    public static String toDisplayText(DifficultyType type, Resources res) {
        String[] texts = res.getStringArray(R.array.difficulty_array);
        return texts[type.ordinal()];
    }

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
