package com.tcd.yaatra.utils.offlinemaps;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.ViaInstruction;
import com.tcd.yaatra.R;
import com.tcd.yaatra.ui.fragments.OfflineMaps;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.services.android.navigation.ui.v5.feedback.FeedbackBottomSheet.TAG;

public class NavEngine {

    NaviVoice naviVoice;
//    private boolean active = false;


    boolean naviVoiceSpoken = false;
//    enum UiJob { Nothing, RecalcPath, UpdateInstruction, Finished };

    private OfflineMaps.UiJob uiJob;
    private int customIcon = R.drawable.ic_my_location_black_24dp;
    private static final int BEST_NAVI_ZOOM = 18;
    private int navIcon = R.drawable.ic_navigation_black_24dp;
    private boolean directTargetDir = false;
    private static final double MAX_WAY_TOLERANCE_METER = 30.0;
    private double partDistanceScaler = 1.0;

    OfflineMaps offlineMaps;


    public NavEngine(OfflineMaps offlineMaps) {
        this.offlineMaps = offlineMaps;
        this.uiJob = offlineMaps.uiJob;
    }

    public void setNavigating(boolean active)
    {
        offlineMaps.active = active;
        if (naviVoice == null)
        {
            naviVoice = new NaviVoice(offlineMaps.getContext());
        }
        if (active == false)
        {
            GeoPoint mcLatLong = new GeoPoint(offlineMaps.mCurrentLocation.getLatitude(), offlineMaps.mCurrentLocation.getLongitude());

            offlineMaps.centerPointOnMap(
                    mcLatLong, 0, 0, 0);
            offlineMaps.setCustomPoint(offlineMaps.getActivity(), mcLatLong, customIcon);
            if (offlineMaps.pos != null)
            {
                GeoPoint curPos = new GeoPoint(offlineMaps.pos.getLatitude(), offlineMaps.pos.getLongitude());
                offlineMaps.centerPointOnMap(curPos, BEST_NAVI_ZOOM, 0, 0);

            }
            NaviDebugSimulator.getSimu().setSimuRun(false);
            return;
        }
        offlineMaps.setCustomPoint(offlineMaps.getActivity(), new GeoPoint(0, 0), navIcon);
        naviVoiceSpoken = false;
        uiJob = OfflineMaps.UiJob.Nothing;
//            initFields(activity);
//            instructions = Navigator.getNavigator().getGhResponse().getInstructions();
        offlineMaps.resetNewInstruction();
        if (offlineMaps.instructions.size() > 0) {
            startDebugSimulator(false);
        }
    }

    public void startDebugSimulator(boolean fromTracking)
    {
        NaviDebugSimulator.getSimu().startDebugSimulator(offlineMaps, offlineMaps.instructions, fromTracking);
    }

    public void updateDirectTargetDir(GeoPoint curPos)
    {
        if (!directTargetDir) { return; }
        joinPathLayerToPos(curPos.getLatitude(), curPos.getLongitude());
    }

    public void joinPathLayerToPos(double lat, double lon)
    {
        try
        {
            List<GeoPoint> geoPoints = new ArrayList<>();
            geoPoints.add(new GeoPoint(lat,lon));
            geoPoints.add(offlineMaps.pathLayer.getPoints().get(1));
            offlineMaps.pathLayer.setPoints(geoPoints);
        }
        catch (Exception e) {
            Log.d(TAG, "joinPathLayerToPos: Error: " + e); }
    }

    @WorkerThread
    public NaviInstruction calculatePosition(GeoPoint curPos)
    {
        PointPosData nearestP = offlineMaps.nearestP;
        InstructionList instructions = offlineMaps.instructions;
        if (uiJob == OfflineMaps.UiJob.RecalcPath) { return null; }
        if (uiJob == OfflineMaps.UiJob.Finished) { return null; }
        nearestP.setBaseData(getNearestPoint(instructions.get(0), nearestP.arrPos, curPos));

        if (nearestP.arrPos > 0)
        { // Check dist to line (backward)
            double lat1 = instructions.get(0).getPoints().getLatitude(nearestP.arrPos);
            double lon1 = instructions.get(0).getPoints().getLongitude(nearestP.arrPos);
            double lat2 = instructions.get(0).getPoints().getLatitude(nearestP.arrPos-1);
            double lon2 = instructions.get(0).getPoints().getLongitude(nearestP.arrPos-1);
            double lDist = GeoMath.distToLineSegment(curPos.getLatitude(), curPos.getLongitude(), lat1, lon1, lat2, lon2);
            if (lDist < nearestP.distance)
            {
                nearestP.distance = lDist;
                nearestP.status = PointPosData.Status.CurPosIsBackward;
            }
        }
        if (nearestP.arrPos < instructions.get(0).getPoints().size()-1)
        { // Check dist to line (forward)
            double lat1 = instructions.get(0).getPoints().getLatitude(nearestP.arrPos);
            double lon1 = instructions.get(0).getPoints().getLongitude(nearestP.arrPos);
            double lat2 = instructions.get(0).getPoints().getLatitude(nearestP.arrPos+1);
            double lon2 = instructions.get(0).getPoints().getLongitude(nearestP.arrPos+1);
            double lDist = GeoMath.distToLineSegment(curPos.getLatitude(), curPos.getLongitude(), lat1, lon1, lat2, lon2);
            if (lDist < nearestP.distance)
            {
                nearestP.distance = lDist;
                nearestP.status = PointPosData.Status.CurPosIsForward;
            }
        }
        else if (nearestP.arrPos == instructions.get(0).getPoints().size()-1 &&
                instructions.size()>1)
        {
            if (instructions.get(1).getPoints().size() > 0)
            { // Check dist to line (forward to next instruction)
                double lat1 = instructions.get(0).getPoints().getLatitude(nearestP.arrPos);
                double lon1 = instructions.get(0).getPoints().getLongitude(nearestP.arrPos);
                double lat2 = instructions.get(1).getPoints().getLatitude(0);
                double lon2 = instructions.get(1).getPoints().getLongitude(0);
                double lDist = GeoMath.distToLineSegment(curPos.getLatitude(), curPos.getLongitude(), lat1, lon1, lat2, lon2);
                if (lDist < nearestP.distance)
                {
                    nearestP.distance = lDist;
                    nearestP.status = PointPosData.Status.CurPosIsForward;
                }
            }
            if (instructions.get(1).getPoints().size() > 1)
            { // Check dist to line (forward next instruction p1+p2)
                double lat1 = instructions.get(1).getPoints().getLatitude(0);
                double lon1 = instructions.get(1).getPoints().getLongitude(0);
                double lat2 = instructions.get(1).getPoints().getLatitude(1);
                double lon2 = instructions.get(1).getPoints().getLongitude(1);
                double lDist = GeoMath.distToLineSegment(curPos.getLatitude(), curPos.getLongitude(), lat1, lon1, lat2, lon2);
                if (lDist < nearestP.distance)
                {
                    nearestP.distance = lDist;
                    nearestP.status = PointPosData.Status.CurPosIsForwardNext;
                }
            }
        }
        if (nearestP.isForward())
        {
            // Reset bearing with calculatePosition()
            nearestP.resetDirectionOk();
        }
        if (!nearestP.isDistanceOk())
        {
            double maxWayTolMeters = MAX_WAY_TOLERANCE_METER;
            if (directTargetDir) { maxWayTolMeters = maxWayTolMeters * 10.0; }
            Instruction nearestNext = instructions.find(curPos.getLatitude(), curPos.getLongitude(), maxWayTolMeters);
            if (nearestNext == null)
            {
                GeoPoint closestP = findClosestStreet(curPos);
                nearestNext = instructions.find(closestP.getLatitude(), closestP.getLongitude(), maxWayTolMeters);
            }
            if (nearestNext == null)
            {
                uiJob = OfflineMaps.UiJob.RecalcPath;
                offlineMaps.recalcFrom = curPos;
                Instruction lastInstruction = instructions.get(instructions.size()-1);
                int lastPoint = lastInstruction.getPoints().size()-1;
                double lastPointLat = lastInstruction.getPoints().getLat(lastPoint);
                double lastPointLon = lastInstruction.getPoints().getLon(lastPoint);
                offlineMaps.recalcTo = new GeoPoint(lastPointLat, lastPointLon);
                Log.d(TAG, "calculatePosition: NaviTask Start recalc !!!!!!");
                return null;
            }
            else
            { // Forward to nearest instruction.
                int deleteCounter = 0;
                Instruction lastDeleted = null;

                while (instructions.size()>0 && !instructions.get(0).equals(nearestNext))
                {
                    deleteCounter++;
                    lastDeleted = instructions.remove(0);
                }
                if (lastDeleted != null)
                { // Because we need the current, and not the next Instruction
                    instructions.add(0, lastDeleted);
                    deleteCounter --;
                }
                if (deleteCounter == 0)
                {
                    PointPosData newNearestP = getNearestPoint(instructions.get(0), 0, curPos);
                    //TODO: Continue-Instruction with DirectionInfo: getContinueInstruction() ?
                    Log.d(TAG, "calculatePosition: NaviTask Start update far !!!!!!");
                    return getUpdatedInstruction(curPos, newNearestP);
                }
                Log.d(TAG, "calculatePosition: NaviTask Start update skip-mult-" + deleteCounter + " !!!!!!");
                return getNewInstruction();
            }
        }
        else if (nearestP.isForwardNext())
        {
            instructions.remove(0);
            Log.d(TAG, "calculatePosition: NaviTask Start skip-next !!!!!!");
            return getNewInstruction();
        }
        else
        {
            // NaviTask Start update!
            return getUpdatedInstruction(curPos, nearestP);
        }
    }

    private static PointPosData getNearestPoint(Instruction instruction, int curPointPos, GeoPoint curPos)
    {
        int nextPointPos = curPointPos;
        int nearestPointPos = curPointPos;
        double nearestDist = Double.MAX_VALUE;
        while (instruction.getPoints().size() > nextPointPos)
        {
            double lat = instruction.getPoints().getLatitude(nextPointPos);
            double lon = instruction.getPoints().getLongitude(nextPointPos);
            double dist = GeoMath.fastDistance(curPos.getLatitude(), curPos.getLongitude(), lat, lon);
            if (dist < nearestDist)
            {
                nearestDist = dist;
                nearestPointPos = nextPointPos;
            }
            nextPointPos++;
        }
        PointPosData p = new PointPosData();
        p.arrPos = nearestPointPos;
        p.distance = nearestDist;
        return p;
    }

    private GeoPoint findClosestStreet(GeoPoint fromPos)
    {
        if (offlineMaps.hopper == null) { return fromPos; } // Not loaded yet!
        QueryResult pos = offlineMaps.hopper.getLocationIndex().findClosest(fromPos.getLatitude(), fromPos.getLongitude(), EdgeFilter.ALL_EDGES);
        int n = pos.getClosestEdge().getBaseNode();
        NodeAccess nodeAccess = offlineMaps.hopper.getGraphHopperStorage().getNodeAccess();
        GeoPoint gp = new GeoPoint(nodeAccess.getLat(n), nodeAccess.getLon(n));
        return gp;
    }

    private NaviInstruction getNewInstruction()
    {
        PointPosData nearestP = offlineMaps.nearestP;
        InstructionList instructions = offlineMaps.instructions;
        nearestP.arrPos = 0;
        nearestP.distance = Double.MAX_VALUE;
        uiJob = OfflineMaps.UiJob.UpdateInstruction;
        if (instructions.size() > 0)
        {

            Instruction in = instructions.get(0);
            long fullTime = countFullTime(in.getTime());
            GeoPoint curPos = new GeoPoint(in.getPoints().getLat(0), in.getPoints().getLon(0));
            double partDistance = countPartDistance(curPos, in, 0);
            if (partDistance == 0) { partDistanceScaler = 1; }
            else
            {
                partDistanceScaler = in.getDistance() / partDistance;
            }
            Instruction nextIn = null;
            if (instructions.size() > 1) { nextIn = instructions.get(1);
            if(nextIn.getExtraInfoJSON().size()!= 0){
                Log.d(TAG, "----------#######-------getNewInstruction--------******--------:--------- " + nextIn.getPoints());
                if(nextIn.getClass().toString().contains("ViaInstruction")){
                    offlineMaps.viaReached = true;
                    offlineMaps.viaCount = ((ViaInstruction) nextIn).getViaCount();
                }
                else if (nextIn.getClass().toString().contains("FinishInstruction")) {
                    offlineMaps.uiJob = OfflineMaps.UiJob.Finished;
                }
            }
            }
            else if (instructions.size() == 1) {
                if(instructions.get(0).getExtraInfoJSON().size()!= 0) {
                    if (instructions.get(0).getClass().toString().contains("FinishInstruction")) {
                        offlineMaps.uiJob = OfflineMaps.UiJob.Finished;
                    }
                }
            }
            NaviInstruction nIn = new NaviInstruction(in, nextIn, fullTime);
            if (speakDistanceCheck(in.getDistance()) && nearestP.isDirectionOk())
            {
                naviVoice.speak(nIn.getVoiceText());
                naviVoiceSpoken = true;
            }
            else
            {
                naviVoiceSpoken = false;
            }
            return nIn;
        }
        uiJob = OfflineMaps.UiJob.Finished;
        return null;
    }

    private long countFullTime(long partTime)
    {
        long fullTime = partTime;
        for (int i=1; i<offlineMaps.instructions.size(); i++)
        {
            fullTime += offlineMaps.instructions.get(i).getTime();
        }
        return fullTime;
    }

    private double countPartDistance(GeoPoint curPos, Instruction in, int nearestPointPos)
    {
        double partDistance = 0;
        double lastLat = curPos.getLatitude();
        double lastLon = curPos.getLongitude();
        for (int i=nearestPointPos+1; i<in.getPoints().size(); i++)
        {
            double nextLat = in.getPoints().getLat(i);
            double nextLon = in.getPoints().getLon(i);
            partDistance += GeoMath.fastDistance(lastLat, lastLon, nextLat, nextLon);
            lastLat = nextLat;
            lastLon = nextLon;
        }
        partDistance = partDistance * GeoMath.METER_PER_DEGREE;
        return partDistance;
    }

    private NaviInstruction getUpdatedInstruction(GeoPoint curPos, PointPosData nearestP)
    {
        InstructionList instructions = offlineMaps.instructions;
        uiJob = OfflineMaps.UiJob.UpdateInstruction;
        if (instructions.size() > 0)
        {
            Instruction in = instructions.get(0);
            long partTime = 0;
            double partDistance = countPartDistance(curPos, in, nearestP.arrPos);
            partDistance = partDistance * partDistanceScaler;
            if (in.getDistance() <= partDistance)
            {
                partDistance = in.getDistance();
                partTime = in.getTime();
            }
            else
            {
                double partValue = partDistance / in.getDistance();
                partTime = (long)(in.getTime() * partValue);
            }
            long fullTime = countFullTime(partTime);
            Instruction nextIn = null;
            if (instructions.size() > 1) { nextIn = instructions.get(1); }
            NaviInstruction newIn = new NaviInstruction(in, nextIn, fullTime);
            newIn.updateDist(partDistance);
            if (!naviVoiceSpoken && nearestP.isDirectionOk() && speakDistanceCheck(partDistance))
            {
                naviVoice.speak(newIn.getVoiceText());
                naviVoiceSpoken = true;
            }
            return newIn;
        }
        return null;
    }

    private boolean speakDistanceCheck(double dist)
    {
        Location pos = offlineMaps.pos;
        if (dist < 200) { return true; }
        if (pos.getSpeed() > 150 * GeoMath.KMH_TO_MSEC)
        {
            if (dist < 1500) { return true; }
        }
        else if (pos.getSpeed() > 100 * GeoMath.KMH_TO_MSEC)
        {
            if (dist < 900) { return true; }
        }
        else if (pos.getSpeed() > 70 * GeoMath.KMH_TO_MSEC)
        {
            if (dist < 500) { return true; }
        }
        else if (pos.getSpeed() > 30 * GeoMath.KMH_TO_MSEC)
        {
            if (dist < 350) { return true; }
        }
        return false;
    }



}
