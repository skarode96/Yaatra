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
import java.util.List;
import android.content.BroadcastReceiver;

import org.json.JSONObject;

public class GetSetDestination extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, SearchView.OnQueryTextListener {

    private MapView mapView;
    private MapboxMap map;
    LocationComponent locationComponent;
    private PermissionsManager permissionsManager;
    private Point destination;
    private Marker destinationMarker;
    SearchView destinationArea;
//    private BroadcastReceiver MyReceiver = null;
    OfflineManager offlineManager;
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private boolean isEndNotified;
    private ProgressBar progressBar;

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
//        MyReceiver= new MyReceiver();
//        broadcastIntent();

    }

//    public void broadcastIntent() {
//        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//    }

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
            //locationComponent.setCameraMode(CameraMode.TRACKING);

            offlineManager = OfflineManager.getInstance(GetSetDestination.this);
            // Create a bounding box for the offline region
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(53.2747, -6.2253)) // Northeast
                    .include(new LatLng(53.2747, -7.2253)) // Southwest
                    .build();

            // Define the offline region
            OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                    loadedMapStyle.getUri(),
                    latLngBounds,
                    10,
                    20,
                    GetSetDestination.this.getResources().getDisplayMetrics().density);
            // Set the metadata
            byte[] metadata;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JSON_FIELD_REGION_NAME, "Yosemite National Park");
                String json = jsonObject.toString();
                metadata = json.getBytes(JSON_CHARSET);
            } catch (Exception exception) {
                Timber.e("Failed to encode metadata: %s", exception.getMessage());
                metadata = null;
            }
            // Create the region asynchronously
            if (metadata != null) {
                offlineManager.createOfflineRegion(
                        definition,
                        metadata,
                        new OfflineManager.CreateOfflineRegionCallback() {
                            @Override
                            public void onCreate(OfflineRegion offlineRegion) {
                                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

// Display the download progress bar
                                progressBar = findViewById(R.id.progress_bar);
                                startProgress();

// Monitor the download progress using setObserver
                                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                    @Override
                                    public void onStatusChanged(OfflineRegionStatus status) {

// Calculate the download percentage and update the progress bar
                                        double percentage = status.getRequiredResourceCount() >= 0
                                                ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                                0.0;

                                        if (status.isComplete()) {
// Download complete
                                            endProgress(getString(R.string.simple_offline_end_progress_success));
                                        } else if (status.isRequiredResourceCountPrecise()) {
// Switch to determinate state
                                            setPercentage((int) Math.round(percentage));
                                        }
                                    }

                                    @Override
                                    public void onError(OfflineRegionError error) {
// If an error occurs, print to logcat
                                        Timber.e("onError reason: %s", error.getReason());
                                        Timber.e("onError message: %s", error.getMessage());
                                    }

                                    @Override
                                    public void mapboxTileCountLimitExceeded(long limit) {
// Notify if offline region exceeds maximum tile count
                                        Timber.e("Mapbox tile count limit exceeded: %s", limit);
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                Timber.e("Error: %s", error);
                            }
                        });
            }
            if (offlineManager != null) {
                offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                    @Override
                    public void onList(OfflineRegion[] offlineRegions) {
                        LatLngBounds bounds = ((OfflineTilePyramidRegionDefinition)
                                offlineRegions[0].getDefinition()).getBounds();
                        double regionZoom = ((OfflineTilePyramidRegionDefinition)
                                offlineRegions[0].getDefinition()).getMinZoom();

// Create new camera position
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(bounds.getCenter())
                                .zoom(regionZoom)
                                .build();

// Move camera to new position
                        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }

                    @Override
                    public void onError(String error) {
                        Timber.e("onListError: %s", error);
                    }
                });
            }

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
//        unregisterReceiver(MyReceiver);
        if (offlineManager != null) {
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    if (offlineRegions.length > 0) {
// delete the last item in the offlineRegions list which will be yosemite offline map
                        offlineRegions[(offlineRegions.length - 1)].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                            @Override
                            public void onDelete() {
                                Toast.makeText(
                                        GetSetDestination.this,
                                        getString(R.string.basic_offline_deleted_toast),
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            @Override
                            public void onError(String error) {
                                Timber.e("On delete error: %s", error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Timber.e("onListError: %s", error);
                }
            });
        }
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
        query = query + ","+locationComponent.getLastKnownLocation().getLongitude()+","+locationComponent.getLastKnownLocation().getLatitude();
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
//        if(isInternetAvailable()){
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(query, 5);
            if (address == null) {
                Toast.makeText(this, "No place found", Toast.LENGTH_SHORT).show();
            }
            Address loc = address.get(0);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 13.0));
            destinationMarker = map.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())));
            destination = Point.fromLngLat(loc.getLongitude(),loc.getLatitude());


        } catch (IOException e) {
            Toast.makeText(this, "Inside catch", Toast.LENGTH_SHORT).show();
            // Get the region bounds and zoom and move the camera.


//        }
//        else
//        {
//            Toast.makeText(this,String.valueOf(isInternetAvailable()),Toast.LENGTH_SHORT).show();
//        }
                e.printStackTrace();
            }
        return true;
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
    // Progress bar methods
    private void startProgress() {

// Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
// Don't notify more than once
        if (isEndNotified) {
            return;
        }

// Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

// Show a toast
        Toast.makeText(GetSetDestination.this, message, Toast.LENGTH_LONG).show();
    }

}
