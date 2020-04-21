package com.tcd.yaatra.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tcd.yaatra.repository.models.TravellerInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;


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

    public static ArrayList<LatLng> sortDestinationsByDistance(LatLng sourceLocation, ArrayList<LatLng> destinations){

        HashMap<Integer, Double> map = new HashMap<>();

        int index = 0;
        for(LatLng dest : destinations){

            double distance = org.apache.lucene.util.SloppyMath.haversinMeters(sourceLocation.getLatitude(), sourceLocation.getLongitude(),
                    dest.getLatitude(), dest.getLongitude());
            map.put(index, distance);

            index++;
        }

        Map<Integer, Double> sorted = map
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));

        ArrayList<LatLng> sortedDestinations = new ArrayList<>();

        for(Map.Entry<Integer, Double> entry : sorted.entrySet()){
            sortedDestinations.add(destinations.get(entry.getKey()));
        }

        return sortedDestinations;
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
