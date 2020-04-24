package com.tcd.yaatra.utils;

import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;

import org.junit.Test;
import org.junit.Ignore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EncryptionUtilsTest {

    private static final String VALUE_SEPARATOR = ",";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    @Ignore
    public void encrypt_EncryptsInputString() {

        String originalText = getSerializedTravellerInfo();
        String encryptedText = EncryptionUtils.encrypt(originalText);
        assertNotEquals(originalText, encryptedText);
    }

    @Test
    @Ignore
    public void decrypt_DecryptsEncryptedText() {

        String originalText = getSerializedTravellerInfo();
        String encryptedText = EncryptionUtils.encrypt(originalText);
        assertNotEquals(originalText, encryptedText);

        String decryptedText = EncryptionUtils.decrypt(encryptedText);

        assertEquals(originalText, decryptedText);
    }

    private String getSerializedTravellerInfo() {
        Integer userId = 1;
        String userName = "TestUser";
        TravellerInfo traveller = new TravellerInfo();
        traveller.setUserId(userId);
        traveller.setUserName(userName);
        traveller.setAge(20);
        traveller.setGender(Gender.MALE);
        traveller.setSourceLatitude(0.0d);
        traveller.setSourceLongitude(0.0d);
        traveller.setDestinationLatitude(0.0d);
        traveller.setDestinationLongitude(0.0d);
        traveller.setStatus(TravellerStatus.SeekingFellowTraveller);
        traveller.setSourceName("test");
        traveller.setDestinationName("test");
        traveller.setModeOfTravel("test");
        traveller.setRequestStartTime(LocalDateTime.now());
        traveller.setUserRating(0.0d);
        traveller.setIpAddress("1.2.3.4");
        traveller.setPortNumber(1234);
        traveller.setStatusUpdateTime(LocalDateTime.now());
        traveller.setInfoProvider(userName);

        return traveller.getAge() + VALUE_SEPARATOR +
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
    }
}
