package com.tcd.yaatra.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.tcd.yaatra.repository.models.TravellerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapUtils {
    public static ArrayList<TravellerInfo> filterFellowTravellers(TravellerInfo userInfo, ArrayList<TravellerInfo> fellowUsersInfo)
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

    public static String locationName(Context context, double latitude, double longitude)
    {
        Geocoder coder = new Geocoder(context);
        List<Address> destinationName;
        String name = null;
        try {
            destinationName = coder.getFromLocation(latitude, longitude, 1);
            name = destinationName.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

}
