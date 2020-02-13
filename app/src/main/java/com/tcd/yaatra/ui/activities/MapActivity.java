package com.tcd.yaatra.ui.activities;

import com.tcd.yaatra.R;
import com.tcd.yaatra.utils.MyReceiver;
import com.tcd.yaatra.databinding.ActivityMapBinding;
import com.tcd.yaatra.ui.viewmodels.MapActivityViewModel;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import androidx.annotation.Nullable;
//import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import timber.log.Timber;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

@SuppressLint("NewApi")
public class MapActivity extends BaseActivity<ActivityMapBinding> implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, SearchView.OnQueryTextListener {

    private MapboxMap map;
    private MapView mapView;
    private OfflineRegion offlineRegion;
    private OfflineManager offlineManager;

    private Point destination;
    private Marker destinationMarker;
    private int regionSelected;
    private BroadcastReceiver MyReceiver = null;
    SearchView destinationArea;
    private Button listButton;
    private Button navigateButton;
    private Button downloadButton;
    LocationComponent locationComponent;
    private PermissionsManager permissionsManager;
    private static final String TAG = "DestinationActivity";
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    @Inject
    MapActivityViewModel MapActivityViewModel;
    //SharedPreferences loginPreferences;

    @Override
    int getLayoutResourceId() { return R.layout.activity_map; }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.listButton.setOnClickListener(view -> handleOnListButtonClick());
        layoutDataBinding.downloadButton.setOnClickListener(view -> handleOnDownloadClick());
        layoutDataBinding.Navigate.setOnClickListener(view -> {
            try {
                handleOnNavigateClick();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(this,getString(R.string.access_token));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //mapView = findViewById(layoutDataBinding.mapViewDestination);
        mapView = findViewById(R.id.mapViewDestination);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        destinationArea = layoutDataBinding.searchDest;
        navigateButton = layoutDataBinding.Navigate;
        destinationArea.setOnQueryTextListener(this);
        MyReceiver= new MyReceiver();
        broadcastIntent();

    }

    @SuppressLint("WrongConstant")
    private void enableLocation(Style loadedMapStyle){

        if(PermissionsManager.areLocationPermissionsGranted(this)){
            LocationComponentOptions.builder(this).build();
            locationComponent = map.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, map.getStyle()).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);

            offlineManager = offlineManager.getInstance(MapActivity.this);
            downloadButton = findViewById(R.id.downloadButton);
            listButton = layoutDataBinding.listButton; //Why is this required!!

        } else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
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
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        map.addOnMapClickListener(this);
        //map.setMinZoomPreference(15);
        //map.setStyle(Style.MAPBOX_STREETS);
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> enableLocation(style));

    }

    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void handleOnListButtonClick() {
        displayOfflineList(mapView, "");// here
    }

    private void handleOnDownloadClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        final EditText regionNameEdit = new EditText(MapActivity.this);
        regionNameEdit.setHint(getString(R.string.set_region_name_hint));

        try {


            // Build the dialog box
            builder.setTitle(getString(R.string.dialog_title))
                    .setView(regionNameEdit)
                    .setMessage(getString(R.string.dialog_message))
                    .setPositiveButton(getString(R.string.dialog_positive_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String regionName = regionNameEdit.getText().toString();
                            // Require a region name to begin the download.
                            // If the user-provided string is empty, display
                            // a toast message and do not begin download.
                            if (regionName.length() == 0) {
                                Toast.makeText(MapActivity.this, getString(R.string.dialog_toast), Toast.LENGTH_SHORT).show();
                            } else {
                                // Begin download process

                                downloadRegion(regionName);
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_negative_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

        }
        catch (Exception ex1)
        {
            ex1.getMessage();

        }
        // Display the dialog
        builder.show();

    }

    private void handleOnNavigateClick() throws UnsupportedEncodingException {
        Intent mapIntent = new Intent(MapActivity.this, RouteInfo.class);
        Bundle bundle = new Bundle();
        double latitude = destination.latitude();
        double longitide = destination.longitude();
        bundle.putDouble("Latitude",latitude);
        bundle.putDouble("Longitude",longitide);
        Toast.makeText(this,String.valueOf(latitude),Toast.LENGTH_SHORT).show();
        mapIntent.putExtras(bundle);
        startActivity(mapIntent);
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

    public void displayOfflineList(View view, final String info) {
        // Build a region list when the user clicks the list button

        // Reset the region selected int to 0
        regionSelected = 0;

        // Query the DB asynchronously
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(final OfflineRegion[] offlineRegions) {
                // Check result. If no regions have been
                // downloaded yet, notify user and return
                if (offlineRegions == null || offlineRegions.length == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_no_regions_yet), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add all of the region names to a list
                ArrayList<String> offlineRegionsNames = new ArrayList<>();
                for (OfflineRegion offlineRegion : offlineRegions) {
                    offlineRegionsNames.add(getRegionName(offlineRegion));
                }
                final CharSequence[] items = offlineRegionsNames.toArray(new CharSequence[offlineRegionsNames.size()]);

                // Build a dialog containing the list of regions
                AlertDialog dialog = new AlertDialog.Builder(MapActivity.this)
                        .setTitle(info + getString(R.string.navigate_title))
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Track which region the user selects
                                regionSelected = which;
                            }
                        })
                        .setPositiveButton(getString(R.string.navigate_positive_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                Toast.makeText(MapActivity.this, items[regionSelected], Toast.LENGTH_LONG).show();

                                // Get the region bounds and zoom
                                LatLngBounds bounds = (offlineRegions[regionSelected].getDefinition()).getBounds();
                                double regionZoom = (offlineRegions[regionSelected].getDefinition()).getMinZoom();

                                // Create new camera position
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(bounds.getCenter())
                                        .zoom(regionZoom)
                                        .build();

                                // Move camera to new position
                                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            }
                        })
                        .setNeutralButton(getString(R.string.navigate_neutral_button_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                // Begin the deletion process
                                offlineRegions[regionSelected].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                                    @Override
                                    public void onDelete() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.toast_region_deleted),
                                                Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Timber.e( "Error: %s", error);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(getString(R.string.navigate_negative_button_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // When the user cancels, don't do anything.
                                // The dialog will automatically close
                            }
                        }).create();
                dialog.show();

            }

            @Override
            public void onError(String error) {
                Timber.e( "Error: %s", error);
            }
        });

    }

    private void downloadRegion( final String regionName) {
        map.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                String styleUrl = style.getUri();
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                double minZoom = map.getCameraPosition().zoom;
                double maxZoom = map.getMaxZoomLevel();
                float pixelRatio = MapActivity.this.getResources().getDisplayMetrics().density;
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
                        MapActivity.this.offlineRegion = offlineRegion;
                        launchDownload();
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

            }
        });

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this,"Location needed to route",Toast.LENGTH_SHORT).show();
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
        navigateButton.setVisibility(View.VISIBLE);

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


        } catch (IOException e) {
            Toast.makeText(this, "Inside catch", Toast.LENGTH_SHORT).show();
            // Get the region bounds and zoom and move the camera.

            View view = this.mapView ;
            displayOfflineList(view , "No Internet available. Please select from ");

        }
        return true;
    }
}
