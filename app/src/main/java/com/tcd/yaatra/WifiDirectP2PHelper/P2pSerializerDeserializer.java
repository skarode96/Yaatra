package com.tcd.yaatra.WifiDirectP2PHelper;

import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;
import com.tcd.yaatra.utils.EncryptionUtils;

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

            String value = traveller.getUserName() + VALUE_SEPARATOR +
                    traveller.getAge() + VALUE_SEPARATOR +
                    traveller.getGender() + VALUE_SEPARATOR +
                    traveller.getSourceLatitude() + VALUE_SEPARATOR +
                    traveller.getSourceLongitude() + VALUE_SEPARATOR +
                    traveller.getDestinationLatitude() + VALUE_SEPARATOR +
                    traveller.getDestinationLongitude() + VALUE_SEPARATOR +
                    traveller.getStatus() + VALUE_SEPARATOR +
                    traveller.getRequestStartTime().format(DATE_TIME_FORMATTER) + VALUE_SEPARATOR +
                    traveller.getUserRating() + VALUE_SEPARATOR +
                    traveller.getIpAddress() + VALUE_SEPARATOR +
                    traveller.getPortNumber() + VALUE_SEPARATOR +
                    traveller.getStatusUpdateTime().format(DATE_TIME_FORMATTER) + VALUE_SEPARATOR +
                    traveller.getInfoProvider();

            String encryptedValue = EncryptionUtils.encrypt(value);

            serializedTravellerInfo.put(traveller.getUserId().toString(), encryptedValue);
        });
        return serializedTravellerInfo;
    }

    public static HashMap<Integer, TravellerInfo> deserializeFromMap(Map<String, String> serializedTravellerInfo){
        HashMap<Integer, TravellerInfo> allTravellers = new HashMap<Integer, TravellerInfo>();

        serializedTravellerInfo.forEach((userId, encryptedValue)->{

            String serializedInfo = EncryptionUtils.decrypt(encryptedValue);

            String[] fieldValues = serializedInfo.split(",", -1);

            TravellerInfo info =
                    new TravellerInfo(Integer.parseInt(userId), fieldValues[0], Integer.parseInt(fieldValues[1])
                    , Gender.valueOf(fieldValues[2])
                    , Double.parseDouble(fieldValues[3]), Double.parseDouble(fieldValues[4])
                    , Double.parseDouble(fieldValues[5]), Double.parseDouble(fieldValues[6])
                    , TravellerStatus.valueOf(fieldValues[7])
                    , LocalDateTime.parse(fieldValues[8], DATE_TIME_FORMATTER)
                    , Double.parseDouble(fieldValues[9])
                    , fieldValues[10]
                    , Integer.parseInt(fieldValues[11])
                    , LocalDateTime.parse(fieldValues[12], DATE_TIME_FORMATTER)
                    , fieldValues[13]);

            allTravellers.put(Integer.parseInt(userId), info);
        });

        return allTravellers;
    }

}
