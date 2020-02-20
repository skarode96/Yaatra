package com.tcd.yaatra.repository.models;

import com.tcd.yaatra.utils.GenderPreferenceUtils;
import com.tcd.yaatra.utils.GenderUtils;

import java.util.HashMap;
import java.util.Map;

public enum Gender {

    NOT_SPECIFIED("No Preference","n",0),
    FEMALE("Female", "f",1),
    MALE("Male", "m",2),
    OTHER("Other", "o",3);

    private static final Map<String, Gender> LABEL = new HashMap<>();
    private static final Map<String, Gender> ID_NAME = new HashMap<>();
    private static final Map<Integer, Gender> ID_NUMBER = new HashMap<>();

    static{
        for(Gender gender:values())
        {
            LABEL.put(gender.stringLabel,gender);
            ID_NAME.put(gender.idName,gender);
            ID_NUMBER.put(gender.idNumber,gender);
        }
    }

    public final String stringLabel;
    public final String idName;
    public final int idNumber;

    private Gender(String label, String value, int idValue) {
        stringLabel = label;
        idName = value;
        idNumber = idValue;
    }

    public static Gender valueOfLabel(String label) {
        return LABEL.get(label);
    }

    public static Gender valueOfIdName(String value) {
        return ID_NAME.get(value);
    }

    public static Gender valueOfIdNumber(int number) {
        return ID_NUMBER.get(number);
    }
}
