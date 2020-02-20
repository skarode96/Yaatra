package com.tcd.yaatra.utils;

import java.util.HashMap;
import java.util.Map;

public enum GenderUtils {

    FEMALE("Female", "f"),
    MALE("Male", "m"),
    OTHER("Other", "o");

    private static final Map<String, GenderUtils> LABEL = new HashMap<>();
    private static final Map<String, GenderUtils> ID_NAME = new HashMap<>();

    static{
        for(GenderUtils gender:values())
        {
            LABEL.put(gender.stringLabel,gender);
            ID_NAME.put(gender.stringValue,gender);
        }
    }

    public final String stringLabel;
    public final String stringValue;

    private GenderUtils(String label, String value) {
        stringLabel = label;
        stringValue = value;
    }

    public static GenderUtils valueOfLabel(String label) {
        return LABEL.get(label);
    }

    public static GenderUtils valueOfIdName(String value) {
        return ID_NAME.get(value);
    }
}
