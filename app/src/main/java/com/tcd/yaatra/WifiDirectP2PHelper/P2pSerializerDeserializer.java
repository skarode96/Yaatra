package com.tcd.yaatra.WifiDirectP2PHelper;

import com.tcd.yaatra.WifiDirectP2PHelper.models.Gender;
import com.tcd.yaatra.WifiDirectP2PHelper.models.TravellerInfo;
import com.tcd.yaatra.WifiDirectP2PHelper.models.TravellerStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class P2pSerializerDeserializer {

    private static final String VALUE_SEPARATOR = ",";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static Map<String, String> serializeToMap(Collection<TravellerInfo> allTravellers){

        Map<String, String> serializedTravellerInfo = new HashMap<>();

        allTravellers.forEach(traveller->{
            serializedTravellerInfo.put(traveller.getUserName(),
                    traveller.getAge() + VALUE_SEPARATOR +
                    traveller.getGender() + VALUE_SEPARATOR +
                    traveller.getSourceLatitude() + VALUE_SEPARATOR +
                    traveller.getSourceLongitude() + VALUE_SEPARATOR +
                    traveller.getDestinationLatitude() + VALUE_SEPARATOR +
                    traveller.getDestinationLongitude() + VALUE_SEPARATOR +
                    traveller.getStatus() + VALUE_SEPARATOR +
                    traveller.getRequestStartTime().format(DATE_TIME_FORMATTER) + VALUE_SEPARATOR +
                    traveller.getIpAddress() + VALUE_SEPARATOR +
                    traveller.getPortNumber());
        });
        return serializedTravellerInfo;
    }

    public static HashMap<String, TravellerInfo> deserializeFromMap(Map<String, String> serializedTravellerInfo){
        HashMap<String, TravellerInfo> allTravellers = new HashMap<>();

        serializedTravellerInfo.forEach((userName, serializedInfo)->{
            TravellerInfo info = new TravellerInfo();
            info.setUserName(userName);

            String[] fieldValues = serializedInfo.split(",", -1);

            info.setAge(Integer.parseInt(fieldValues[0]));
            info.setGender(Gender.valueOf(fieldValues[1]));
            info.setSourceLatitude(Double.parseDouble(fieldValues[2]));
            info.setSourceLongitude(Double.parseDouble(fieldValues[3]));
            info.setDestinationLatitude(Double.parseDouble(fieldValues[4]));
            info.setDestinationLongitude(Double.parseDouble(fieldValues[5]));
            info.setStatus(TravellerStatus.valueOf(fieldValues[6]));
            info.setRequestStartTime(LocalDateTime.parse(fieldValues[7], DATE_TIME_FORMATTER));
            info.setIpAddress(fieldValues[8]);
            info.setPortNumber(Integer.parseInt(fieldValues[9]));

            allTravellers.put(userName, info);
        });

        return allTravellers;
    }

}
