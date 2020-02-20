package com.tcd.yaatra.repository.models;

import java.util.HashMap;
import java.util.Map;

public enum TransportPreference {

    NO_PREFERENCE("No Preference",0),
    WALK("Walk", 1),
    TAXI("Taxi", 2);

    private static final Map<String, TransportPreference> LABEL = new HashMap<>();
    private static final Map<Integer, TransportPreference> ID_NUMBER = new HashMap<>();

    static{
        for(TransportPreference transport:values())
        {
            LABEL.put(transport.stringLabel,transport);
            ID_NUMBER.put(transport.intValue,transport);
        }
    }

    public final String stringLabel;
    public final int intValue;

    private TransportPreference(String label, int value) {
        stringLabel = label;
        intValue = value;
    }

    public static TransportPreference valueOfLabel(String label) {
        return LABEL.get(label);
    }

    public static TransportPreference valueOfIdNumber(int number) {
        return ID_NUMBER.get(number);
    }
}
