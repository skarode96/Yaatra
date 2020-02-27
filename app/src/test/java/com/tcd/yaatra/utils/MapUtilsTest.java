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

    TravellerInfo ti1 = new TravellerInfo(1, travellerUserName, 20, Gender.MALE, 0.0, 0.0, 0.0d, 0.0 , TravellerStatus.SeekingFellowTraveller, LocalDateTime.now(), 0.0, "1.2.3.4", 12345, LocalDateTime.now(), userName);
    TravellerInfo ti2 = new TravellerInfo(1, travellerUserName, 20, Gender.MALE, 0.0, 0.0, 0.0d, 0.0 , TravellerStatus.SeekingFellowTraveller, LocalDateTime.now(), 0.0, "1.2.3.4", 12345, LocalDateTime.now(), userName);
    TravellerInfo ti3 = new TravellerInfo(1, travellerUserName, 20, Gender.MALE, 0.0, 0.0, 0.0d, 0.0 , TravellerStatus.SeekingFellowTraveller, LocalDateTime.now(), 0.0, "1.2.3.4", 12345, LocalDateTime.now(), userName);
    TravellerInfo tiu = new TravellerInfo(1, travellerUserName, 20, Gender.MALE, 0.0, 0.0, 0.0d, 0.0 , TravellerStatus.SeekingFellowTraveller, LocalDateTime.now(), 0.0, "1.2.3.4", 12345, LocalDateTime.now(), userName);
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
}