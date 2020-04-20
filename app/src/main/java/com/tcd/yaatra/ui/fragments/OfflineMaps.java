package com.tcd.yaatra.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;
import com.graphhopper.util.shapes.GHPoint;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tcd.yaatra.R;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.databinding.FragmentOfflineMapsBinding;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.ui.activities.UserRatingActivity;
import com.tcd.yaatra.utils.offlinemaps.GHAsyncTask;
import com.tcd.yaatra.utils.offlinemaps.InstructionCalculation;
import com.tcd.yaatra.utils.offlinemaps.KalmanLocationManager;
import com.tcd.yaatra.utils.offlinemaps.NavEngine;
import com.tcd.yaatra.utils.offlinemaps.NaviInstruction;
import com.tcd.yaatra.utils.offlinemaps.PointPosData;
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

import javax.inject.Inject;

import static com.mapbox.services.android.navigation.ui.v5.feedback.FeedbackBottomSheet.TAG;


public class OfflineMaps extends BaseFragment<FragmentOfflineMapsBinding> {

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_offline_maps;
    }

    private static final int BUFFER_SIZE = 8 * 1024;
    private static final int BEST_NAVI_ZOOM = 18;
    public float tiltMult = 1.0f;
    public float tiltMultPos = 1.0f;
    private LocationManager locationManager;
    private KalmanLocationManager kalmanLocationManager;
    private MapView mapView;
    MapPosition tmpPos = new MapPosition();
    private boolean needLocation = false;
    private MapFileTileSource tileSource;
    private ItemizedLayer<MarkerItem> itemizedLayer;
    private ItemizedLayer<MarkerItem> customLayer;
    public GraphHopper hopper;
    public static Location mCurrentLocation;
    private Location mLastLocation;
    PointList trackingPointList = new PointList();
    private int customIcon = R.drawable.ic_my_location_black_24dp;
    private int destIcon = R.drawable.ic_place_black_24dp;
    private int navIcon = R.drawable.ic_navigation_black_24dp;
    public PathLayer pathLayer;
    private int previousIcon;
    String modeOfTravel;

    public Location pos;
    enum UiJob { Nothing, RecalcPath, UpdateInstruction, Finished };
    private UiJob uiJob = UiJob.Nothing;
    public InstructionList instructions;
    private static final double MAX_WAY_TOLERANCE = 0.000008993 * 30.0;
    private ViewGroup navTopVP;

    private ImageView navtop_image;
    private TextView navtop_curloc;
    private TextView navtop_nextloc;
    private TextView navtop_when;
    private TextView navtop_time;

    public boolean active = false;
    public PointPosData nearestP = new PointPosData();
    private GHAsyncTask<GeoPoint, NaviInstruction, NaviInstruction> naviEngineTask;
    public GeoPoint recalcFrom, recalcTo;
    private TravellerInfo[] users;


    private NavEngine navEngine = new NavEngine(this);
    private InstructionCalculation instructionCalculation = new InstructionCalculation(this);

    @Inject
    TravellerInfo ownTravellerInfo;

    @Inject
    PeerCommunicator peerCommunicator;

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.fabNav.setOnClickListener(view -> handleOnNavClick(view));
        layoutDataBinding.navtopStop.setOnClickListener(view -> handleOnStopNavClick(view));
    }

    private void handleOnStopNavClick(View view) {
        layoutDataBinding.fabNav.setEnabled(true);
        navEngine.setNavigating(false);
//        Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.fragment_container);
        pathLayer = null;
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();

    }

    private void handleOnNavClick(View view) {

        navTopVP.setVisibility(View.VISIBLE);
        layoutDataBinding.fabNav.setEnabled(false);
        navEngine.setNavigating(true);
        Location curLoc = mLastLocation;
        if (curLoc!=null)
        {
            updatePosition(getActivity(), curLoc);
        }

    }

    @UiThread
    public void updatePosition(Activity activity, Location pos)
    {
        if (!active) { return; }
        if (uiJob == UiJob.RecalcPath) { return; }
        if (this.pos == null) { this.pos = new Location((String)null); }
        this.pos.set(pos);
        GeoPoint curPos = new GeoPoint(pos.getLatitude(), pos.getLongitude());
        GeoPoint newCenter = curPos.destinationPoint(70.0 * tiltMultPos, pos.getBearing());
        centerPointOnMap(newCenter, BEST_NAVI_ZOOM, 360.0f - pos.getBearing(), 45.0f * tiltMult);

        calculatePositionAsync(activity, curPos);
    }

    @UiThread
    private void calculatePositionAsync(Activity activity, GeoPoint curPos)
    {
        if (naviEngineTask == null) { createNaviEngineTask(activity); }
        navEngine.updateDirectTargetDir(curPos);
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

    @UiThread
    private void createNaviEngineTask(final Activity activity)
    {
        naviEngineTask = new GHAsyncTask<GeoPoint, NaviInstruction, NaviInstruction>()
        {
            @Override
            protected NaviInstruction saveDoInBackground(GeoPoint... params) throws Exception
            {
                return navEngine.calculatePosition(params[0]);
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
                            calcPath(points, getActivity(), modeOfTravel);
                        }
                    }
                    else if (uiJob == UiJob.Finished)
                    {
                        active = false;
                        Toast.makeText(getContext(),"finished job",Toast.LENGTH_SHORT).show();
                    }
                }
                else if (uiJob == UiJob.UpdateInstruction)
                {
                    showInstruction(in);
                }
            }
        };
    }

    @UiThread
    public void resetNewInstruction()
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
            instructionCalculation.setTiltMult(1);
        }
        else if(nearestP.isDirectionOk())
        {
            navtop_when.setText(in.getNextDistanceString());
            navtop_time.setText(in.getFullTimeString());
            navtop_curloc.setText(in.getCurStreet());
            navtop_nextloc.setText(in.getNextInstruction());
            navtop_image.setImageResource(in.getNextSignResource());
            instructionCalculation.setTiltMult(in.getNextDistance());
        }
        else
        {
            navtop_when.setText("0 " + UnitCalculator.getUnit(false));
            navtop_time.setText(in.getFullTimeString());
            navtop_curloc.setText(R.string.wrong_direction);
            navtop_nextloc.setText("==================");
            // TOCHANGE ARROW TO ROUNDABOUT
            navtop_image.setImageResource(R.drawable.ic_up_arrow);
            instructionCalculation.setTiltMult(1);
        }
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


        if(getArguments()!=null)
        {
            ArrayList<LatLng> travelPath  = getArguments().getParcelableArrayList("destLocations");

            /*double latitude = ownTravellerInfo.getDestinationLatitude();
            double longitude = ownTravellerInfo.getDestinationLongitude();
            String modeOfTravel =  ownTravellerInfo.getModeOfTravel();*/
//            Boolean multiDestination = getArguments().getBoolean("multiDestination");
            Boolean isGroupOwner = getArguments().getBoolean("IsGroupOwner");

            Bundle bundle = getArguments();
            double latitude = ownTravellerInfo.getDestinationLatitude();
            double longitude = ownTravellerInfo.getDestinationLongitude();
            modeOfTravel =  ownTravellerInfo.getModeOfTravel();

            if(modeOfTravel.equalsIgnoreCase("driving"))
            {
                modeOfTravel="car";
            }
            else if(modeOfTravel.equalsIgnoreCase("walking"))
            {
                modeOfTravel="foot";
            }
            else if(modeOfTravel.equalsIgnoreCase("cycling"))
            {
                modeOfTravel="bike";
            }
            Boolean multiDestination = bundle.getBoolean("multiDestination");
            Gson gson = new Gson();
            points.add(new GHPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            users = gson.fromJson(bundle.getString("UserList"), TravellerInfo[].class);
            if(multiDestination){
                ArrayList<LatLng> locations  = bundle.getParcelableArrayList("destLocations");
                for(int i = 1; i< locations.size(); i++)
                {
                    points.add(new GHPoint(locations.get(i).getLatitude(), locations.get(i).getLongitude()));
                    GeoPoint destLatLong = new GeoPoint(locations.get(i).getLatitude(), locations.get(i).getLongitude());
                    setCustomPoint(getActivity(), destLatLong, destIcon);
                }
            }
            else {

                points.add(new GHPoint(latitude, longitude));
                GeoPoint destLatLong = new GeoPoint(latitude, longitude);
                setCustomPoint(getActivity(), destLatLong, destIcon);
            }
        }
        else
        {
            points.add(new GHPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            points.add(new GHPoint(53.280355, -6.214713));
            points.add(new GHPoint(53.33999864, -6.25499898));

            GeoPoint destLatLong = new GeoPoint(53.280355, -6.214713);
            GeoPoint dest2LatLong = new GeoPoint(53.33999864, -6.25499898);
            setCustomPoint(getActivity(), destLatLong, destIcon);
            setCustomPoint(getActivity(), dest2LatLong, destIcon);
            modeOfTravel="car";
        }

        calcPath(points, getActivity(),modeOfTravel);
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

            centerPointOnMap(
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

    public void centerPointOnMap(GeoPoint latLong, int zoomLevel, float bearing, float tilt)
    {
        if (zoomLevel == 0)
        {
            zoomLevel = mapView.map().getMapPosition().zoomLevel;

        }
        double scale = 1 << zoomLevel;
        tmpPos.setPosition(latLong);
        tmpPos.setScale(scale);
        tmpPos.setBearing(bearing);
        tmpPos.setTilt(tilt);
        mapView.map().animator().animateTo(300, tmpPos);
    }

    public void calcPath(final List<GHPoint> points, final Activity activity, String modeOfTravel) {


        new AsyncTask<Void, Void, GHResponse>() {
            float time;

            @Override
            protected GHResponse doInBackground(Void... v) {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(points).
                        setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().put(Parameters.Routing.INSTRUCTIONS, true);
                req.setVehicle(modeOfTravel);
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

}
