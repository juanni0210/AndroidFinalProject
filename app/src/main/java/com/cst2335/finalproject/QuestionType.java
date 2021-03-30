package com.cst2335.finalproject;

import android.content.res.Resources;

public enum QuestionType {
    Any, Multiple, TrueOrFalse;

    public static String toDisplayText(QuestionType type, Resources res) {
        String[] texts = res.getStringArray(R.array.question_type_array);
        return texts[type.ordinal()];
    }

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
