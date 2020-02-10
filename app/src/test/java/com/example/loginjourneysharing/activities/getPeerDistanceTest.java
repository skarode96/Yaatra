package com.example.loginjourneysharing.activities;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class getPeerDistanceTest {


    double lat1 = 53.343901;
    double long1 = -6.255591;
    double lat2 = 53.341629;
    double long2 = -6.252580;
    double distance = 0 ;
    TravellerInfo ti = new TravellerInfo();
    ArrayList<TravellerInfo> tl = new ArrayList<TravellerInfo>();


    getPeerDistance getpeerdistance = new getPeerDistance();

    @Test
    public void getFellowTravelers() {
        this.ti.setDestinationLat(0.943249);     // Different
        this.ti.setDestinationLong(-0.955898);
        this.tl.add(this.ti);
        this.ti.setDestinationLat(53.343249);
        this.ti.setDestinationLong(-6.255898);
        this.tl.add(this.ti);
        this.ti.setDestinationLat(53.343249);
        this.ti.setDestinationLong(-6.255898);
        this.tl.add(this.ti);
        this.ti.setDestinationLat(53.343249);
        this.ti.setDestinationLong(-6.255898);
        ArrayList<TravellerInfo> ul = new ArrayList<TravellerInfo>();

        ul = getpeerdistance.getFellowTravelers(ti, tl);

        assertEquals(ul.size(),2);
    }
    @Test
    public void calculateDistanceInMeters() {


        distance = getpeerdistance.calculateDistanceInMeters(lat1, long1, lat2, long2);
        //Log.e("calculateDistanceInMeters", " Distance " + distance );

        assertEquals(322.1495962406575, distance , 0);
    }


}