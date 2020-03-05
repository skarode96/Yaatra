package com.tcd.yaatra.repository.models;

import java.util.HashMap;
import java.util.Map;

public enum JourneyFrequency {

    DAILY("Daily",0),
    WEEKLY("Weekly", 1),
    WEEKEND("Weekend", 2);

    private static final Map<String, JourneyFrequency> LABEL = new HashMap<>();
    private static final Map<Integer, JourneyFrequency> ID_NUMBER = new HashMap<>();

    static{
        for(JourneyFrequency transport:values())
        {
            LABEL.put(transport.stringLabel,transport);
            ID_NUMBER.put(transport.intValue,transport);
        }
    }

    public final String stringLabel;
    public final int intValue;

    private JourneyFrequency(String label, int value) {
        stringLabel = label;
        intValue = value;
    }

    public static JourneyFrequency valueOfLabel(String label) {
        return LABEL.get(label);
    }

    public static JourneyFrequency valueOfIdNumber(int number) {
        return ID_NUMBER.get(number);
    }
}
