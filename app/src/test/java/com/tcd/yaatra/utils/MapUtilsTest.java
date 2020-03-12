package com.tcd.yaatra.utils;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;

public class MapUtilsTest {

    String travellerUserName = "Traveller";
    String userName = "TestUser";

    TravellerInfo ti1 = getTraveller();
    TravellerInfo ti2 = getTraveller();
    TravellerInfo ti3 = getTraveller();
    TravellerInfo tiu = getTraveller();
    ArrayList<TravellerInfo> tl = new ArrayList<TravellerInfo>();

    MapUtils maputils = new MapUtils();

    @Test
    public void getFellowTravelers() {
        this.ti1.setDestinationLatitude(0.943249);     // Different
        this.ti1.setDestinationLongitude(-0.955898);
        this.tl.add(this.ti1);
        this.ti2.setDestinationLatitude(53.343249);
        this.ti2.setDestinationLongitude(-6.255898);
        this.tl.add(this.ti2);
        this.ti3.setDestinationLatitude(53.343249);
        this.ti3.setDestinationLongitude(-6.255898);
        this.tl.add(this.ti3);
        this.tiu.setDestinationLatitude(53.343249);
        this.tiu.setDestinationLongitude(-6.255898);
        ArrayList<TravellerInfo> ul = new ArrayList<>();
        ul = maputils.filterFellowTravellers(tiu, tl);
        assertEquals(2,ul.size());
    }

    private TravellerInfo getTraveller(){
        TravellerInfo traveller = new TravellerInfo();
        traveller.setUserId(1);
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

        return traveller;
    }
}