package com.example.loginjourneysharing.activities;

import org.junit.Test;
import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;import static org.junit.Assert.assertEquals;

public class MapActivityTest {




//    private Point originPosition;
//    private Point destinationPosition;
//    private Point loc1;
//    private Context context = ApplicationProvider.getApplicationContext();
//
//    @Test
//    public void showRoute() {
//        originPosition = Point.fromLngLat(53.343792, -6.254572);
//        destinationPosition = Point.fromLngLat(53.2747, -6.2253);
//        Geocoder coder = new Geocoder(context);
//        List<Address> address;
//        try {
//            address = coder.getFromLocationName("Trinity College, Dublin", 5);
//            if (address == null) {
//                return;
//            }
//            Address loc = address.get(0);
//            loc1 = Point.fromLngLat(loc.getLongitude(), loc.getLatitude());
//            assertEquals(originPosition, loc1);
//
////        NavigationRoute.builder(context)
////                .accessToken(Mapbox.getAccessToken())
////                .origin(originPosition)
////                .destination(destinationPosition)
////                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
////                .build()
////                .getRoute(new Callback<DirectionsResponse>() {
////                    @Override
////                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
////                        System.out.println("Routes "+response.body().routes().get(0));
////                    }
////
////                    @Override
////                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
////
////                    }
////                });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}