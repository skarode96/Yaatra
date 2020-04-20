package com.tcd.yaatra.utils.offlinemaps;

import android.location.Location;
import android.util.Log;

import com.graphhopper.util.Instruction;
import com.tcd.yaatra.ui.fragments.OfflineMaps;

import org.oscim.core.GeoPoint;

import static com.mapbox.services.android.navigation.ui.v5.feedback.FeedbackBottomSheet.TAG;

public class PointPosData {

    private static final double MAX_WAY_TOLERANCE = 0.000008993 * 30.0;
    public enum Status { CurPosIsExactly, CurPosIsBackward, CurPosIsForward, CurPosIsForwardNext };
    public int arrPos;
    public double distance;
    public Status status = Status.CurPosIsExactly;
    private boolean wrongDir = false;
    private boolean wrongDirHint = false;
    public boolean isDistanceOk()
    {
        return (distance < MAX_WAY_TOLERANCE);
    }
    public boolean isBackward() { return (status == Status.CurPosIsBackward); }
    public boolean isForward() { return (status == Status.CurPosIsForward); }
    public boolean isForwardNext() { return (status == Status.CurPosIsForwardNext); }
    public boolean isDirectionOk() { return (!wrongDir); }
    public void resetDirectionOk()
    {
        if (!isDistanceOk()) { return; }
        wrongDirHint = false;
        wrongDir = false;
    }
    public void checkDirectionOk(Location pos, Instruction in, NaviVoice v)
    {
        calculateWrongDir(pos, in);
        if (wrongDir)
        {
            if (wrongDirHint) { return; }
            wrongDirHint = true;
            v.speak("Wrong direction");
        }
    }

    private void calculateWrongDir(Location pos, Instruction in)
    {
        if (in.getPoints().size()<2) { return; }
        if (!wrongDir)
        {
            GeoPoint pathP1 = new GeoPoint(in.getPoints().getLat(0), in.getPoints().getLon(0));
            GeoPoint pathP2 = new GeoPoint(in.getPoints().getLat(1), in.getPoints().getLon(1));
            double bearingOk = pathP1.bearingTo(pathP2);
            double bearingCur = pos.getBearing();
            double bearingDiff = bearingOk - bearingCur;
            if (bearingDiff < 0) { bearingDiff += 360.0; } //Normalize
            if (bearingDiff > 180) { bearingDiff = 360.0 - bearingDiff; } //Normalize
            wrongDir = (bearingDiff > 100);
            Log.d(TAG, "Compare bearing cur=" + bearingCur + " way=" + bearingOk + " wrong=" + wrongDir);
        }
    }

    public void setBaseData(PointPosData p)
    {
        this.arrPos = p.arrPos;
        this.distance = p.distance;
        this.status = p.status;
        if (arrPos > 0)
        {
            resetDirectionOk();
        }
    }
}
