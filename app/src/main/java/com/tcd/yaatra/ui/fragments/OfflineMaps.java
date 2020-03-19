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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;
import com.graphhopper.util.shapes.GHPoint;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentOfflineMapsBinding;
import com.tcd.yaatra.utils.offlinemaps.GHAsyncTask;
import com.tcd.yaatra.utils.offlinemaps.KalmanLocationManager;

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
    private int customIcon = R.drawable.ic_my_location_dark_24dp;
    private int destIcon = R.drawable.ic_location_start_24dp ;
    private PathLayer pathLayer;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mapView = (MapView) this.layoutDataBinding.mapview;

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

            centerPointOnMap(
                    mcLatLong, 0, 0, 0);
            setCustomPoint(getActivity(), mcLatLong, customIcon);
        }
    }

    public void setCustomPoint(Activity activity, GeoPoint p, int icon)
    {
        if (customLayer==null) { return; }
        if (p!=null)
        {
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
                    int sWidth = 4;
                    pathLayer = updatePathLayer(activity, pathLayer, resp.getPoints(), "#0EB5D3", sWidth);
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
