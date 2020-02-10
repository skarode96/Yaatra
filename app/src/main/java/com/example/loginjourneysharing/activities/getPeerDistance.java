package com.example.loginjourneysharing.activities;

import android.util.Log;
//import com.tcd.yaatra.WifiDirectP2PHelper.models.TravellerInfo;


import com.example.loginjourneysharing.R;

import java.util.ArrayList;

public class getPeerDistance {


    // Lat long distance calculation
    public double calculateDistanceInMeters(double lat1, double long1, double lat2, double long2) {


        double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
        return dist;
    }


    public ArrayList<TravellerInfo> getFellowTravelers (TravellerInfo userInfo, ArrayList<TravellerInfo> fellowUsersInfo)
    {
        int nearLimit = R.string.fellowUserDistance ;
        ArrayList<TravellerInfo> nearUsersList = new ArrayList<TravellerInfo>();
        double distance =0;
        for (TravellerInfo fellowUserInfo : fellowUsersInfo)
        {
            distance = calculateDistanceInMeters(userInfo.getDestinationLat(), userInfo.getDestinationLong(),
                                                 fellowUserInfo.getDestinationLat(), fellowUserInfo.getDestinationLong());

            if (distance < nearLimit)
            {
                nearUsersList.add(fellowUserInfo);
            }
        }
        return nearUsersList;
    }
}
