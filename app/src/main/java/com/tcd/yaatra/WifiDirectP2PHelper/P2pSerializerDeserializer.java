package com.tcd.yaatra.WifiDirectP2PHelper;

import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;
import com.tcd.yaatra.utils.EncryptionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class P2pSerializerDeserializer {

    private static final String VALUE_SEPARATOR = ",";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static List<Map<String, String>> serializeToMap(Collection<TravellerInfo> allTravellers) {

        List<Map<String, String>> serializedTravellerRecords = new ArrayList<>();

        allTravellers.forEach(traveller -> {

            String value = traveller.getUserName() + VALUE_SEPARATOR +
                    traveller.getAge() + VALUE_SEPARATOR +
                    traveller.getGender() + VALUE_SEPARATOR +
                    traveller.getSourceLatitude() + VALUE_SEPARATOR +
                    traveller.getSourceLongitude() + VALUE_SEPARATOR +
                    traveller.getDestinationLatitude() + VALUE_SEPARATOR +
                    traveller.getDestinationLongitude() + VALUE_SEPARATOR +
                    traveller.getStatus() + VALUE_SEPARATOR +
                    traveller.getSourceName() + VALUE_SEPARATOR +
                    traveller.getDestinationName() + VALUE_SEPARATOR +
                    traveller.getModeOfTravel() + VALUE_SEPARATOR +
                    traveller.getRequestStartTime().format(DATE_TIME_FORMATTER) + VALUE_SEPARATOR +
                    traveller.getUserRating() + VALUE_SEPARATOR +
                    traveller.getIpAddress() + VALUE_SEPARATOR +
                    traveller.getPortNumber() + VALUE_SEPARATOR +
                    traveller.getStatusUpdateTime().format(DATE_TIME_FORMATTER) + VALUE_SEPARATOR +
                    traveller.getInfoProvider();

            String encryptedValue = EncryptionUtils.encrypt(value);

            HashMap<String, String> map = new HashMap<>();
            map.put(traveller.getUserId().toString(), encryptedValue);

            serializedTravellerRecords.add(map);
        });

        return serializedTravellerRecords;
    }

    public static HashMap<Integer, TravellerInfo> deserializeFromMap(Map<String, String> serializedTravellerInfo) {
        HashMap<Integer, TravellerInfo> allTravellers = new HashMap<Integer, TravellerInfo>();

        serializedTravellerInfo.forEach((userId, encryptedValue) -> {

            String serializedInfo = EncryptionUtils.decrypt(encryptedValue);

            String[] fieldValues = serializedInfo.split(",", -1);

            TravellerInfo traveller = new TravellerInfo();
            traveller.setUserId(Integer.parseInt(userId));
            traveller.setUserName(fieldValues[0]);
            traveller.setAge(Integer.parseInt(fieldValues[1]));
            traveller.setGender(Gender.valueOf(fieldValues[2]));
            traveller.setSourceLatitude(Double.parseDouble(fieldValues[3]));
            traveller.setSourceLongitude(Double.parseDouble(fieldValues[4]));
            traveller.setDestinationLatitude(Double.parseDouble(fieldValues[5]));
            traveller.setDestinationLongitude(Double.parseDouble(fieldValues[6]));
            traveller.setStatus(TravellerStatus.valueOf(fieldValues[7]));
            traveller.setSourceName(fieldValues[8]);
            traveller.setDestinationName(fieldValues[9]);
            traveller.setModeOfTravel(fieldValues[10]);
            traveller.setRequestStartTime(LocalDateTime.parse(fieldValues[11], DATE_TIME_FORMATTER));
            traveller.setUserRating(Double.parseDouble(fieldValues[12]));
            traveller.setIpAddress(fieldValues[13]);
            traveller.setPortNumber(Integer.parseInt(fieldValues[14]));
            traveller.setStatusUpdateTime(LocalDateTime.parse(fieldValues[15], DATE_TIME_FORMATTER));
            traveller.setInfoProvider(fieldValues[16]);

            allTravellers.put(Integer.parseInt(userId), traveller);
        });

        return allTravellers;
    }

}
