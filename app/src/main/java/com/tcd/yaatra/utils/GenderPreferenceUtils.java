package com.tcd.yaatra.utils;

import java.util.HashMap;
import java.util.Map;

public enum GenderPreferenceUtils {
    NO_PREFERENCE("No Preference",0),
    FEMALE("Female", 1),
    MALE("Male", 2),
    Other("Other", 3);

    private static final Map<String, GenderPreferenceUtils> LABEL = new HashMap<>();
    private static final Map<Integer, GenderPreferenceUtils> ID_NUMBER = new HashMap<>();

    static{
        for(GenderPreferenceUtils gender:values())
        {
            LABEL.put(gender.stringLabel,gender);
            ID_NUMBER.put(gender.intValue,gender);
        }
    }

    public final String stringLabel;
    public final int intValue;

    private GenderPreferenceUtils(String label, int value) {
        stringLabel = label;
        intValue = value;
    }

    public static GenderPreferenceUtils valueOfLabel(String label) {
        return LABEL.get(label);
    }

    public static GenderPreferenceUtils valueOfIdNumber(int number) {
        return ID_NUMBER.get(number);
    }
}
