package com.tcd.yaatra.utils.offlinemaps;

import android.location.Location;
import android.util.Log;

import com.tcd.yaatra.ui.fragments.OfflineMaps;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.services.android.navigation.ui.v5.feedback.FeedbackBottomSheet.TAG;

public class InstructionCalculation {

//    private Location pos;

    OfflineMaps offlineMaps;

    public InstructionCalculation(OfflineMaps offlineMaps) {
        this.offlineMaps = offlineMaps;
    }

    public void setTiltMult(double nextDist)
    {
        double speedXtra = 0;
        if (offlineMaps.pos!=null) { speedXtra = offlineMaps.pos.getSpeed(); }
        if (speedXtra > 30.0) { speedXtra = 2; }
        else if (speedXtra < 8.0) { speedXtra = 0; }
        else
        {
            speedXtra = speedXtra - 8.0; // 0 - 22
            speedXtra = speedXtra / 22.0; // 0 - 1
            speedXtra = speedXtra * 2.0; // 0 - 2
        }
        if (nextDist > 400) { nextDist = 0; }
        else if (nextDist < 100) { nextDist = 0; }
        else
        {
            nextDist = nextDist - 100.0; // 0 - 300
            nextDist = nextDist / 300.0; // 0 - 1
            nextDist = nextDist * 2.0; // 0 - 2
        }
        if (speedXtra > nextDist) { nextDist = speedXtra; }
        offlineMaps.tiltMultPos = (float)(1.0 + (nextDist * 0.5));
        offlineMaps.tiltMult = (float)(1.0 + nextDist);
    }





}
