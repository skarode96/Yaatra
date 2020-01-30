package com.example.loginjourneysharing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.loginjourneysharing.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import android.content.BroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class GetSetDestination extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, SearchView.OnQueryTextListener {

    private MapView mapView;
    private MapboxMap map;
    LocationComponent locationComponent;
    private PermissionsManager permissionsManager;
    private Point destination;
    private Marker destinationMarker;
    SearchView destinationArea;
    private BroadcastReceiver MyReceiver = null;

    //offline Objects
    private OfflineManager offlineManager;
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private OfflineRegion offlineRegion;
    private int regionSelected = 0;
    private Button downloadButton;
    private Button listButton;

    private static final String TAG = "GetSetDestinationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.access_token));
        setContentView(R.layout.activity_get_set_destination);
        mapView = findViewById(R.id.mapViewDestination);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        destinationArea = (SearchView) findViewById(R.id.searchDest);
        destinationArea.setOnQueryTextListener(this);
        MyReceiver= new MyReceiver();
        broadcastIntent();

    }

    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        map.addOnMapClickListener(this);
//        map.setMinZoomPreference(15);
//        map.setStyle(Style.MAPBOX_STREETS);
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocation(style);

            }
        });

    }

    @SuppressLint("WrongConstant")
    private void enableLocation(Style loadedMapStyle){

        if(PermissionsManager.areLocationPermissionsGranted(this)){
            LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(this).build();
            locationComponent = map.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, map.getStyle()).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);

            offlineManager = offlineManager.getInstance(GetSetDestination.this);
            downloadButton = findViewById(R.id.downloadButton);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this,"Location needed to route",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation(map.getStyle());
        }
        else
        {
            Toast.makeText(this,"Permissions not granted",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        unregisterReceiver(MyReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        if(destinationMarker != null)
        {
            map.removeMarker(destinationMarker);
        }

        destinationMarker = map.addMarker(new MarkerOptions().position(point));
        destination = Point.fromLngLat(point.getLongitude(),point.getLatitude());

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        final String name = query;
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        List<Address> countries;
        try {


            //query = query + ","+locationComponent.getLastKnownLocation().getLongitude()+","+locationComponent.getLastKnownLocation().getLatitude();
            countries = coder.getFromLocation(locationComponent.getLastKnownLocation().getLatitude(),locationComponent.getLastKnownLocation().getLongitude(),1);
            if(countries.size()>0)
            {
                query = query + "," + countries.get(0).getCountryName();
            }
            else
            {
                Toast.makeText(this, "No country found", Toast.LENGTH_SHORT).show();
                return false;
                //query = query + ","+locationComponent.getLastKnownLocation().getLongitude()+","+locationComponent.getLastKnownLocation().getLatitude();
            }
            final String regionName = query;
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
//        if(isInternetAvailable()){


            address = coder.getFromLocationName(query, 5);
            if (address == null) {
                Toast.makeText(this, "No place found", Toast.LENGTH_SHORT).show();
                return false;
            }
            Address loc = address.get(0);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 13.0));
//            destinationMarker = map.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())));
            destination = Point.fromLngLat(loc.getLongitude(),loc.getLatitude());

            map.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    String styleUrl = style.getUri();
                    LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                    double minZoom = map.getMinZoomLevel();
                    double maxZoom = map.getMaxZoomLevel();
                    float pixelRatio = GetSetDestination.this.getResources().getDisplayMetrics().density;
                    OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(styleUrl,bounds,minZoom,maxZoom,pixelRatio);

                    byte[] metadata = null;
                    try{
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(JSON_FIELD_REGION_NAME,regionName);
                        String json = jsonObject.toString();
                        metadata = json.getBytes(JSON_CHARSET);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        metadata = null;
                    }

                    offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
                        @Override
                        public void onCreate(OfflineRegion offlineRegion) {
                            Log.d(TAG, "onCreate: Offline region to be created"+regionName);
                            GetSetDestination.this.offlineRegion = offlineRegion;
                            launchDownload();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });

                }
            });


        } catch (IOException e) {
            Toast.makeText(this, "Inside catch", Toast.LENGTH_SHORT).show();
            // Get the region bounds and zoom and move the camera.


                offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                    @Override
                    public void onList(OfflineRegion[] offlineRegions) {
                        if (offlineRegions == null || offlineRegions.length == 0) {
                            Toast.makeText(getApplicationContext(), "You have no regions yet.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ArrayList<String> offlineRegionNames = new ArrayList<>();
                        ArrayList<String> offlineSelectedRegionNames = new ArrayList<>();
                        for (OfflineRegion offlineRegion : offlineRegions) {
                            String regName = getRegionName(offlineRegion);
                            offlineRegionNames.add(regName);
                            if (regName.toLowerCase().contains(name.toLowerCase())) {
                                offlineSelectedRegionNames.add(regName);
                            }
                        }
                        if (offlineSelectedRegionNames.size() == 0) {
                            Toast.makeText(getApplicationContext(), "No offline region found", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (offlineSelectedRegionNames.size() == 1) {
                            int index = offlineRegionNames.indexOf(offlineSelectedRegionNames.get(0));
                            LatLngBounds bounds = (offlineRegions[index].getDefinition().getBounds());
                            double regionZoom = (offlineRegions[index].getDefinition().getMinZoom());

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(bounds.getCenter())
                                    .zoom(regionZoom)
                                    .build();

                            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } else {
                            final CharSequence[] items = offlineSelectedRegionNames.toArray(new CharSequence[offlineRegionNames.size()]);

                        }


                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
//        }
//        else
//        {
//            Toast.makeText(this,String.valueOf(isInternetAvailable()),Toast.LENGTH_SHORT).show();
//        }
                //e.printStackTrace();
            }
        return true;
    }



    private String getRegionName(OfflineRegion offlineRegion) {
        String regionName;
         try{
             byte[] metadata = offlineRegion.getMetadata();
             String json = new String(metadata, JSON_CHARSET);
             JSONObject jsonObject = new JSONObject(json);
             regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
         } catch (Exception e) {
             Log.d(TAG, "getRegionName: Failed to decode metadata "+e.getMessage());
             regionName = String.format("Region %1$d", offlineRegion.getID());
         }
         return regionName;
    }

    private void launchDownload() {

        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {
                if(status.isComplete())
                {
                    Log.d(TAG, "onStatusChanged: Offline region downloaded successfully");
                }
            }

            @Override
            public void onError(OfflineRegionError error) {
                Log.d(TAG, "onError message: "+error.getMessage());
                Log.d(TAG, "onError reason: "+error.getReason());

            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                Log.d(TAG, "mapboxTileCountLimitExceeded: "+limit);
            }
        });
        offlineRegion.setDownloadState(offlineRegion.STATE_ACTIVE);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void showRoutes(View v) throws UnsupportedEncodingException {

        Intent mapIntent = new Intent(GetSetDestination.this, MapActivity.class);
        Bundle bundle = new Bundle();
        double latitude = destination.latitude();
        double longitide = destination.longitude();
        bundle.putDouble("Latitude",latitude);
        bundle.putDouble("Longitude",longitide);
        Toast.makeText(this,String.valueOf(latitude),Toast.LENGTH_SHORT).show();
        mapIntent.putExtras(bundle);
        startActivity(mapIntent);

    }

}
