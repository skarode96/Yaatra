package com.tcd.yaatra.utils;

import com.tcd.yaatra.repository.models.TravellerInfo;

import java.util.ArrayList;


public class MapUtils {
    private ArrayList<TravellerInfo> getFellowTravelers (TravellerInfo userInfo, ArrayList<TravellerInfo> fellowUsersInfo)
    {
        int nearLimit = 1000; //In meters
        ArrayList<TravellerInfo> nearUsersList = new ArrayList<>();
        double distance;
        for (TravellerInfo fellowUserInfo : fellowUsersInfo)
        {
            distance = org.apache.lucene.util.SloppyMath.haversinMeters(userInfo.getDestinationLatitude(), userInfo.getDestinationLongitude(),
                    fellowUserInfo.getDestinationLatitude(), fellowUserInfo.getDestinationLongitude());

            if (distance < nearLimit)
            {
                nearUsersList.add(fellowUserInfo);
            }
        }
        return nearUsersList;
    }

}
