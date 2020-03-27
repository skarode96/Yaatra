package com.tcd.yaatra.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;
import com.graphhopper.util.shapes.GHPoint;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentOfflineMapsBinding;
import com.tcd.yaatra.utils.offlinemaps.GHAsyncTask;
import com.tcd.yaatra.utils.offlinemaps.GeoMath;
import com.tcd.yaatra.utils.offlinemaps.KalmanLocationManager;
import com.tcd.yaatra.utils.offlinemaps.NaviDebugSimulator;
import com.tcd.yaatra.utils.offlinemaps.NaviInstruction;
import com.tcd.yaatra.utils.offlinemaps.NaviVoice;
import com.tcd.yaatra.utils.offlinemaps.UnitCalculator;

import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.mapbox.services.android.navigation.ui.v5.feedback.FeedbackBottomSheet.TAG;


public class OfflineMaps extends BaseFragment<FragmentOfflineMapsBinding> {
    
    

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_offline_maps;
    }

    public static final int BUFFER_SIZE = 8 * 1024;
    private static final int BEST_NAVI_ZOOM = 18;
    private float tiltMult = 1.0f;
    private float tiltMultPos = 1.0f;
    private LocationManager locationManager;
    private KalmanLocationManager kalmanLocationManager;
    private MapView mapView;
    MapPosition tmpPos = new MapPosition();
    private boolean needLocation = false;
    private MapFileTileSource tileSource;
    private ItemizedLayer<MarkerItem> itemizedLayer;
    private ItemizedLayer<MarkerItem> customLayer;
    private GraphHopper hopper;
    private static Location mCurrentLocation;
    private Location mLastLocation;
    PointList trackingPointList = new PointList();
    private int customIcon = R.drawable.ic_my_location_black_24dp;
    private int destIcon = R.drawable.ic_place_black_24dp;
    private int navIcon = R.drawable.ic_navigation_black_24dp;
    private PathLayer pathLayer;
    private int previousIcon;

    private Location pos;
    boolean naviVoiceSpoken = false;
    enum UiJob { Nothing, RecalcPath, UpdateInstruction, Finished };
    private UiJob uiJob = UiJob.Nothing;
    private InstructionList instructions;
    private static final double MAX_WAY_TOLERANCE = 0.000008993 * 30.0;
    private ViewGroup navTopVP;

    private ImageView navtop_image;
    private TextView navtop_curloc;
    private TextView navtop_nextloc;
    private TextView navtop_when;
    private TextView navtop_time;

    NaviVoice naviVoice;
    private boolean active = false;
    final PointPosData nearestP = new PointPosData();
    GHAsyncTask<GeoPoint, NaviInstruction, NaviInstruction> naviEngineTask;
    private GeoPoint recalcFrom, recalcTo;
    private boolean directTargetDir = false;
    private static final double MAX_WAY_TOLERANCE_METER = 30.0;
    private double partDistanceScaler = 1.0;

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.fabNav.setOnClickListener(view -> handleOnNavClick(view));
    }

    private void handleOnNavClick(View view) {

        navTopVP.setVisibility(View.VISIBLE);
        setNavigating(true);
        Location curLoc = mLastLocation;
        if (curLoc!=null)
        {
            updatePosition(getActivity(), curLoc);
        }

    }

    @UiThread
    public void updatePosition(Activity activity, Location pos)
    {
        if (active == false) { return; }
        if (uiJob == UiJob.RecalcPath) { return; }
        if (this.pos == null) { this.pos = new Location((String)null); }
        this.pos.set(pos);
        GeoPoint curPos = new GeoPoint(pos.getLatitude(), pos.getLongitude());
        GeoPoint newCenter = curPos.destinationPoint(70.0 * tiltMultPos, pos.getBearing());
        centerPointOnMap(mapView,newCenter, BEST_NAVI_ZOOM, 360.0f - pos.getBearing(), 45.0f * tiltMult);

        calculatePositionAsync(activity, curPos);
    }

    @UiThread
    private void calculatePositionAsync(Activity activity, GeoPoint curPos)
    {
        if (naviEngineTask == null) { createNaviEngineTask(activity); }
        updateDirectTargetDir(curPos);
        if (naviEngineTask.getStatus() == AsyncTask.Status.RUNNING)
        {
            Log.d(TAG, "calculatePositionAsync: Error, NaviEngineTask is still running! Drop job ...");
        }
        else if (naviEngineTask.hasError())
        {
            naviEngineTask.getError().printStackTrace();
        }
        else
        {
            createNaviEngineTask(activity); //TODO: Recreation of Asynctask seems necessary?!
            naviEngineTask.execute(curPos);
        }
    }

    private void updateDirectTargetDir(GeoPoint curPos)
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
            geoPoints.add(pathLayer.getPoints().get(1));
            pathLayer.setPoints(geoPoints);
        }
        catch (Exception e) {
            Log.d(TAG, "joinPathLayerToPos: Error: " + e); }
    }

    @UiThread
    private void createNaviEngineTask(final Activity activity)
    {
        naviEngineTask = new GHAsyncTask<GeoPoint, NaviInstruction, NaviInstruction>()
        {
            @Override
            protected NaviInstruction saveDoInBackground(GeoPoint... params) throws Exception
            {
                return calculatePosition(params[0]);
            }

            @Override
            protected void onPostExecute(NaviInstruction in)
            {
                if (in == null)
                {
                    if (uiJob == UiJob.RecalcPath)
                    {
                        if (instructions != null)
                        {
                            instructions = null;
                            List<GHPoint> points = new ArrayList<>();
                            points.add(new GHPoint(recalcFrom.getLatitude(), recalcFrom.getLongitude()));
                            points.add(new GHPoint(recalcTo.getLatitude(), recalcTo.getLongitude()));
                            calcPath(points, getActivity());
                        }
                    }
                    else if (uiJob == UiJob.Finished)
                    {
                        active = false;
                    }
                }
                else if (uiJob == UiJob.UpdateInstruction)
                {
                    showInstruction(in);
                }
            }
        };
    }

    @WorkerThread
    private NaviInstruction calculatePosition(GeoPoint curPos)
    {
        if (uiJob == UiJob.RecalcPath) { return null; }
        if (uiJob == UiJob.Finished) { return null; }
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
                uiJob = UiJob.RecalcPath;
                recalcFrom = curPos;
                Instruction lastInstruction = instructions.get(instructions.size()-1);
                int lastPoint = lastInstruction.getPoints().size()-1;
                double lastPointLat = lastInstruction.getPoints().getLat(lastPoint);
                double lastPointLon = lastInstruction.getPoints().getLon(lastPoint);
                recalcTo = new GeoPoint(lastPointLat, lastPointLon);
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

    private NaviInstruction getNewInstruction()
    {
        nearestP.arrPos = 0;
        nearestP.distance = Double.MAX_VALUE;
        uiJob = UiJob.UpdateInstruction;
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
            if (instructions.size() > 1) { nextIn = instructions.get(1); }
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
        uiJob = UiJob.Finished;
        return null;
    }

    private long countFullTime(long partTime)
    {
        long fullTime = partTime;
        for (int i=1; i<instructions.size(); i++)
        {
            fullTime += instructions.get(i).getTime();
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
        uiJob = UiJob.UpdateInstruction;
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
        if (hopper == null) { return fromPos; } // Not loaded yet!
        QueryResult pos = hopper.getLocationIndex().findClosest(fromPos.getLatitude(), fromPos.getLongitude(), EdgeFilter.ALL_EDGES);
        int n = pos.getClosestEdge().getBaseNode();
        NodeAccess nodeAccess = hopper.getGraphHopperStorage().getNodeAccess();
        GeoPoint gp = new GeoPoint(nodeAccess.getLat(n), nodeAccess.getLon(n));
        return gp;
    }

    public void setNavigating(boolean active)
    {
            this.active = active;
            if (naviVoice == null)
            {
                naviVoice = new NaviVoice(getContext());
            }
            if (active == false)
            {
                GeoPoint mcLatLong = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                centerPointOnMap(mapView,
                        mcLatLong, 0, 0, 0);
                setCustomPoint(getActivity(), mcLatLong, customIcon);
                if (pos != null)
                {
                    GeoPoint curPos = new GeoPoint(pos.getLatitude(), pos.getLongitude());
                    centerPointOnMap(mapView,curPos, BEST_NAVI_ZOOM, 0, 0);

                }
                NaviDebugSimulator.getSimu().setSimuRun(false);
                return;
            }
            setCustomPoint(getActivity(), new GeoPoint(0, 0), navIcon);
            naviVoiceSpoken = false;
            uiJob = UiJob.Nothing;
//            initFields(activity);
//            instructions = Navigator.getNavigator().getGhResponse().getInstructions();
            resetNewInstruction();
            if (instructions.size() > 0) {
                startDebugSimulator(getActivity(), false);
            }
    }

    public void startDebugSimulator(Activity activity, boolean fromTracking)
    {
        NaviDebugSimulator.getSimu().startDebugSimulator(this, instructions, fromTracking);
    }

    @UiThread
    private void resetNewInstruction()
    {
        nearestP.arrPos = 0;
        nearestP.distance = Double.MAX_VALUE;
        uiJob = UiJob.UpdateInstruction;
        showInstruction(null);
    }

    @UiThread
    private void showInstruction(NaviInstruction in)
    {
        if (in==null)
        {
            navtop_when.setText("0 " + UnitCalculator.getUnit(false));
            navtop_time.setText("--------");
            navtop_curloc.setText(R.string.search_location);
            navtop_nextloc.setText("==================");
            navtop_image.setImageResource(R.drawable.ic_up_arrow);
            setTiltMult(1);
        }
        else if(nearestP.isDirectionOk())
        {
            navtop_when.setText(in.getNextDistanceString());
            navtop_time.setText(in.getFullTimeString());
            navtop_curloc.setText(in.getCurStreet());
            navtop_nextloc.setText(in.getNextInstruction());
            navtop_image.setImageResource(in.getNextSignResource());
            setTiltMult(in.getNextDistance());
        }
        else
        {
            navtop_when.setText("0 " + UnitCalculator.getUnit(false));
            navtop_time.setText(in.getFullTimeString());
            navtop_curloc.setText(R.string.wrong_direction);
            navtop_nextloc.setText("==================");
            // TOCHANGE ARROW TO ROUNDABOUT
            navtop_image.setImageResource(R.drawable.ic_up_arrow);
            setTiltMult(1);
        }
    }

    private void setTiltMult(double nextDist)
    {
        double speedXtra = 0;
        if (pos!=null) { speedXtra = pos.getSpeed(); }
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
        tiltMultPos = (float)(1.0 + (nextDist * 0.5));
        tiltMult = (float)(1.0 + nextDist);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mapView = (MapView) this.layoutDataBinding.mapview;
        navTopVP = (ViewGroup) view.findViewById(R.id.navtop_layout);
        navtop_image = (ImageView) this.layoutDataBinding.navtopImage;
        navtop_curloc = (TextView) this.layoutDataBinding.navtopCurloc;
        navtop_nextloc = (TextView) this.layoutDataBinding.navtopNextloc;
        navtop_when = (TextView) this.layoutDataBinding.navtopWhen;
        navtop_time = (TextView) this.layoutDataBinding.navtopTime;

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        kalmanLocationManager = new KalmanLocationManager(getContext());
        kalmanLocationManager.setMaxPredictTime(10000);

        BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, Intent intent) {

                if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                    unZipFiles();
                    initMap();
                }

            }
        };

        File apkStorage = null;

        DownloadManager dm = (DownloadManager) getActivity().getSystemService(getContext().DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://vsrv15044.customer.xenway.de/maps/maps/2019-10/europe_ireland-and-northern-ireland.ghz"));

        apkStorage = new File(
                Environment.getExternalStorageDirectory() + "/"
                        + "Yaatra Downloads");
        if (!apkStorage.exists()) {
            apkStorage.mkdir();
        }
        File outputFile = new File(apkStorage, "ireland_map.ghz");

        File zipFile = new File(apkStorage, "ireland_map-gh");


        if (!outputFile.exists()) {

            request.setDestinationUri(Uri.fromFile(outputFile));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/yaatra");
            long enqueueId = dm.enqueue(request);
            File idFile = new File(apkStorage, "ireland_map.id");

            try(FileWriter sw = new FileWriter(idFile, false);
                BufferedWriter bw = new BufferedWriter(sw))
            {
                bw.write("" + enqueueId);
                bw.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            getContext().registerReceiver(downloadReceiver, filter);


        }
        else if (!zipFile.exists()) {
            unZipFiles();
            initMap();
        }
        else {
            initMap();
        }

        return view;

    }

    public void initMap() {
        mapView.setClickable(true);
        File apkStorage = new File(
                Environment.getExternalStorageDirectory() + "/"
                        + "Yaatra Downloads");
        File zipFile = new File(apkStorage, "ireland_map-gh");
        loadMap(zipFile, this);
        checkGpsAvailability();
        ensureLastLocationInit();
        updateCurrentLocation(mLastLocation);

        List<GHPoint> points = new ArrayList<>();
        points.add(new GHPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        points.add(new GHPoint(53.280355, -6.214713));
        points.add(new GHPoint(53.33999864, -6.25499898));

        calcPath(points, getActivity());
        GeoPoint destLatLong = new GeoPoint(53.280355, -6.214713);
        GeoPoint dest2LatLong = new GeoPoint(53.33999864, -6.25499898);
        setCustomPoint(getActivity(), destLatLong, destIcon);
        setCustomPoint(getActivity(), dest2LatLong, destIcon);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getActivity().finish();
        }
        else {
            Log.d(TAG, "onRequestPermissionsResult: No access");
        }
    }

    private void updateCurrentLocation(Location location) {
        if (location != null) {
            mCurrentLocation = location;
        } else if (mLastLocation != null && mCurrentLocation == null) {
            mCurrentLocation = mLastLocation;
        }
        if (mCurrentLocation != null) {
            GeoPoint mcLatLong = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            centerPointOnMap(mapView,
                    mcLatLong, 0, 0, 0);
            setCustomPoint(getActivity(), mcLatLong, customIcon);
        }
    }

    public void setCustomPoint(Activity activity, GeoPoint p, int icon)
    {
        if (customLayer==null) {
            return; }
        if(previousIcon == navIcon) {
            customLayer.removeItem(customLayer.getItemList().size() - 1);
        }

        if (p!=null)
        {
            previousIcon = icon;
            customLayer.addItem(createMarkerItem(activity, p, icon, 0.5f, 0.5f));
            mapView.map().updateMap(true);
        }
    }

    private MarkerItem createMarkerItem(Activity activity, GeoPoint p, int resource, float offsetX, float offsetY) {
        Drawable drawable = ContextCompat.getDrawable(activity, resource);
        Bitmap bitmap = AndroidGraphics.drawableToBitmap(drawable);
        MarkerSymbol markerSymbol = new MarkerSymbol(bitmap, offsetX, offsetY);
        MarkerItem markerItem = new MarkerItem("", "", p);
        markerItem.setMarker(markerSymbol);
        return markerItem;
    }

    private void ensureLastLocationInit()
    {
        if (mLastLocation != null) { return; }
        try
        {
            Location lonet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lonet != null) { mLastLocation = lonet; return; }
        }
        catch (SecurityException|IllegalArgumentException e)
        {
            Log.d(TAG, "NET-Location is not supported: "+e.getMessage());
        }
        try
        {
            Location logps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (logps != null) { mLastLocation = logps; return; }
        }
        catch (SecurityException|IllegalArgumentException e)
        {
            Log.d(TAG, "GPS-Location is not supported: "+e.getMessage());
        }
    }

    private void checkGpsAvailability() {
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("Select Map Automatically");
            builder1.setCancelable(true);
            builder1.setTitle("GPS is off");
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int buttonNr)
                {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(intent);
                }
            };
            builder1.setPositiveButton("GPS settings", listener);
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void loadMap(File path, OfflineMaps activity) {

        tileSource = new MapFileTileSource();
        tileSource.setMapFile(new File(path,  "europe_ireland-and-northern-ireland.map").getAbsolutePath());
        VectorTileLayer l = mapView.map().setBaseMap(tileSource);
        mapView.map().setTheme(VtmThemes.DEFAULT);
        mapView.map().layers().add(new BuildingLayer(mapView.map(), l));
        mapView.map().layers().add(new LabelLayer(mapView.map(), l));

        itemizedLayer = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
        mapView.map().layers().add(itemizedLayer);
        customLayer = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
        mapView.map().layers().add(customLayer);

        GeoPoint mapCenter = tileSource.getMapInfo().boundingBox.getCenterPoint();
        mapView.map().setMapPosition(mapCenter.getLatitude(), mapCenter.getLongitude(), 1 << 12);

        loadGraphStorage();

    }

    void loadGraphStorage() {
        new GHAsyncTask<Void, Void, Path>() {
            protected Path saveDoInBackground(Void... v) throws Exception {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.getCHFactoryDecorator().addCHProfileAsString("shortest");
                tmpHopp.load(new File(Environment.getExternalStorageDirectory() + "/"
                        + "Yaatra Downloads/ireland_map") + "-gh");
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute(Path o) {
                if (hasError()) {
                    Log.d(TAG, "An error happened while creating graph:" + getErrorMessage());
                } else {
                    Log.d(TAG, "AFinished loading graph.");
                }
            }
        }.execute();
    }


    void unZipFiles(){
        ZipInputStream zipIn = null;
        try {
            zipIn = new ZipInputStream(new FileInputStream(Environment.getExternalStorageDirectory() + "/"
                    + "Yaatra Downloads/ireland_map.ghz"));
            File mapFolder = new File(Environment.getExternalStorageDirectory() + "/"
                    + "Yaatra Downloads/ireland_map" + "-gh");
            File destDir = new File(mapFolder.getAbsolutePath());
            if (destDir.exists()) {
                recursiveDelete(destDir);
            }
            if (!destDir.exists()) {
                destDir.mkdir();
            }
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = mapFolder.getAbsolutePath() + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipIn, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (zipIn!=null) {
                try {
                    zipIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void recursiveDelete(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) for (File child : fileOrDirectory.listFiles())
            recursiveDelete(child);
        try {
            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractFile(ZipInputStream zipIn,
                             String filePath) throws IOException {
        BufferedOutputStream bos = null;
        try{
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
        finally
        {
            if (bos!=null) bos.close();
        }
    }

    private void requestPermission(String[] sPermission){
        if(getActivity() != null)
            ActivityCompat.requestPermissions(getActivity(), sPermission, 1);
    }

    private static boolean checkPermission(String sPermission, Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, sPermission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void centerPointOnMap(MapView mapview,GeoPoint latLong, int zoomLevel, float bearing, float tilt)
    {
        if (zoomLevel == 0)
        {
            zoomLevel = mapview.map().getMapPosition().zoomLevel;
        }
        double scale = 1 << zoomLevel;
        tmpPos.setPosition(latLong);
        tmpPos.setScale(scale);
        tmpPos.setBearing(bearing);
        tmpPos.setTilt(tilt);
        mapview.map().animator().animateTo(300, tmpPos);
    }

    public void calcPath(final List<GHPoint> points, final Activity activity) {


        new AsyncTask<Void, Void, GHResponse>() {
            float time;

            @Override
            protected GHResponse doInBackground(Void... v) {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(points).
                        setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().put(Parameters.Routing.INSTRUCTIONS, true);
                req.setVehicle("car");
                req.setWeighting("shortest");
                GHResponse resp = null;
                if (hopper != null)
                {
                    resp = hopper.route(req);
                }
                time = sw.stop().getSeconds();
                return resp;
            }


            @Override
            protected void onPostExecute(GHResponse ghResp) {
                if (!ghResp.hasErrors()) {
                    PathWrapper resp = ghResp.getBest();
                    instructions = resp.getInstructions();
                    int sWidth = 4;
                    pathLayer = updatePathLayer(activity, pathLayer, resp.getPoints(), "#800EA3BF", sWidth);
                    mapView.map().updateMap(true);
                }
            }
        }.execute();
    }

    private PathLayer updatePathLayer(Activity activity, PathLayer ref, PointList pointList, String color, int strokeWidth) {
        if (ref==null) {
            ref = createPathLayer(activity, color, strokeWidth);
            mapView.map().layers().add(ref);
        }
        List<GeoPoint> geoPoints = new ArrayList<>();
        //TODO: Search for a more efficient way
        for (int i = 0; i < pointList.getSize(); i++)
            geoPoints.add(new GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)));
        ref.setPoints(geoPoints);
        return ref;
    }

    private PathLayer createPathLayer(Activity activity, String color, int strokeWidth)
    {
        Style style = Style.builder()
                .fixed(true)
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(color)
                .strokeWidth(strokeWidth * activity.getResources().getDisplayMetrics().density)
                .build();
        PathLayer newPathLayer = new PathLayer(mapView.map(), style);
        return newPathLayer;
    }


    static class PointPosData
    {
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

}
