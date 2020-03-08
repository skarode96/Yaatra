package com.tcd.yaatra.mocks;

import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;

import java.util.ArrayList;

public class PeerTravellerInfoMocks {

    public static ArrayList<TravellerInfo> getPeerTravellerList() {
        ArrayList<TravellerInfo> travellerInfos = new ArrayList<>();
        travellerInfos.add(new TravellerInfo(1,"Jaya", Gender.FEMALE, 12.12, 22.12, 33.13,43.13, "Bus"));
        travellerInfos.add(new TravellerInfo(2,"Guddu", Gender.MALE, 13.12, 23.12, 33.13,43.13, "Taxi"));
        travellerInfos.add(new TravellerInfo(3,"Reshma", Gender.FEMALE, 14.12, 24.12, 33.13,43.13, "Train"));
        travellerInfos.add(new TravellerInfo(4,"Pintu", Gender.MALE, 15.12, 25.12, 33.13,43.13, "Walk"));
        travellerInfos.add(new TravellerInfo(5,"Neha", Gender.FEMALE, 16.12, 26.12, 33.13,43.13, "Train"));
        return travellerInfos;
    }
}
