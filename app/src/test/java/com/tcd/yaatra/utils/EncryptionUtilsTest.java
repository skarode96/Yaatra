package com.tcd.yaatra.utils;

import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EncryptionUtilsTest {

    private static final String VALUE_SEPARATOR = ",";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    public void encrypt_EncryptsInputString(){

        String originalText = getSerializedTravellerInfo();
        String encryptedText = EncryptionUtils.encrypt(originalText);
        assertNotEquals(originalText, encryptedText);
    }

    @Test
    public void decrypt_DecryptsEncryptedText(){

        String originalText = getSerializedTravellerInfo();
        String encryptedText = EncryptionUtils.encrypt(originalText);
        assertNotEquals(originalText, encryptedText);

        String decryptedText = EncryptionUtils.decrypt(encryptedText);

        assertEquals(originalText, decryptedText);
    }

    private String getSerializedTravellerInfo(){
        String userName = "TestUser";
        TravellerInfo traveller = new TravellerInfo(userName, 20, Gender.Male, 0.0d
                , 0.0d, 0.0d, 0.0d
                , TravellerStatus.SeekingFellowTraveller, LocalDateTime.now(), 0.0d
                , "1.2.3.4", 12345, LocalDateTime.now(), userName);

        return  traveller.getAge() + VALUE_SEPARATOR +
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
    }
}
