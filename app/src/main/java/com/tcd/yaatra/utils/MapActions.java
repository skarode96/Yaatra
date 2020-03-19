package com.tcd.yaatra.utils;

import android.view.View;
import android.view.ViewGroup;

import com.tcd.yaatra.ui.fragments.OfflineMaps;

import org.oscim.core.GeoPoint;

public class MapActions implements MapHandlerListener{

    enum TabAction{ StartPoint, EndPoint, AddFavourit, None };
    private TabAction tabAction = TabAction.None;
    private ViewGroup sideBarVP, sideBarMenuVP, southBarSettVP, southBarFavourVP, navSettingsVP, navSettingsFromVP, navSettingsToVP,
            navInstructionListVP, navTopVP;

    @Override
    public void onPressLocation(GeoPoint latLong) {
//        if (tabAction == TabAction.None) { return; }
//        if (tabAction == TabAction.AddFavourit)
//        {
//            sideBarVP.setVisibility(View.VISIBLE);
//            tabAction = TabAction.None;
//            GeoPoint[] points = new GeoPoint[3];
//            points[2] = latLong;
//            String[] names = new String[3];
//            names[2] = "Selected position";
////            startGeocodeActivity(points, names, false, true);
//            return;
//        }
        String text = "" + latLong.getLatitude() + ", " + latLong.getLongitude();
        doSelectCurrentPos(latLong, text);
//        tabAction = TabAction.None;
    }

    @Override
    public void pathCalculating(boolean shortestPathRunning) {

    }

    private void doSelectCurrentPos(GeoPoint newPos, String text)
    {
//        if (isStartP)
//        {
//            Destination.getDestination().setStartPoint(newPos, text);
//            fromLocalET.setText(text);
//            addFromMarker(Destination.getDestination().getStartPoint(), true);
//            navSettingsFromVP.setVisibility(View.INVISIBLE);
//        }
//        else
//        {
//            Destination.getDestination().setEndPoint(newPos, text);
//            toLocalET.setText(text);
//            addToMarker(Destination.getDestination().getEndPoint(), true);
//            navSettingsToVP.setVisibility(View.INVISIBLE);
//        }
//        setQuickButtonsClearVisible(isStartP, true);
//        sideBarVP.setVisibility(View.INVISIBLE);
//        if (!activateNavigator())
//        {
//            navSettingsVP.setVisibility(View.VISIBLE);
//        }
        OfflineMaps offlineMaps = new OfflineMaps();
        offlineMaps.centerPointOnMap(newPos, 0, 0, 0);
    }
}
