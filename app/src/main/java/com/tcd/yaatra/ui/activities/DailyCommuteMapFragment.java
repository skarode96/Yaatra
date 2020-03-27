package com.tcd.yaatra.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentDailyCommuteMapBinding;
import com.tcd.yaatra.ui.fragments.BaseFragment;
import com.tcd.yaatra.ui.fragments.CreateDailyCommuteFragment;
import com.tcd.yaatra.ui.fragments.DailyFragment;

import java.io.IOException;
import java.util.List;

public class DailyCommuteMapFragment extends BaseFragment<FragmentDailyCommuteMapBinding> implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, SearchView.OnQueryTextListener {

    private MapboxMap map;
    private MapView mapView;
    private Marker marker;
    boolean destinationIndicator;
    SearchView destinationArea;
    LocationComponent locationComponent;
    Button fab;
    private Point dailySource;
    private Point dailyDestination;
    private PermissionsManager permissionsManager;
    View view;

    @Override
    public int getFragmentResourceId() { return R.layout.fragment_daily_commute_map; }


    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.dailySearch.setOnQueryTextListener(this);
        layoutDataBinding.addDestination.setOnClickListener(view -> handleOnFABButtonClick(view));
        layoutDataBinding.journeyPref.setOnClickListener(view -> handleOnJourneyPrefs());
    }

    private void handleOnJourneyPrefs() {
        if(marker == null)
            Toast.makeText(getActivity(),"Add location marker",Toast.LENGTH_SHORT).show();
        else {
            Intent dailyCommuteIntent = new Intent(getActivity(), CreateDailyCommuteFragment.class);
            Bundle bundle = new Bundle();
            if(dailySource != null && dailyDestination != null)
            {
                bundle.putDouble("sourceLat",dailySource.latitude());
                bundle.putDouble("sourceLong",dailySource.longitude());
                bundle.putDouble("destinationLat",dailyDestination.latitude());
                bundle.putDouble("destinationLong",dailyDestination.longitude());
            }
            dailyCommuteIntent.putExtras(bundle);
            Fragment f = new CreateDailyCommuteFragment();
            f.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,f).commit();
        }
    }

    @SuppressLint("RestrictedApi")

    private void handleOnFABButtonClick(View view) {

        if(marker == null)
            Toast.makeText(getActivity(),"Add location marker",Toast.LENGTH_SHORT).show();
        else {
            layoutDataBinding.dailySearch.setQueryHint("Enter Destination Region");
            layoutDataBinding.addDestination.setVisibility(View.GONE);
            layoutDataBinding.journeyPref.setVisibility(View.VISIBLE);
            map.removeMarker(marker);
            marker=null;
            destinationIndicator = true;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(),getString(R.string.access_token));
        view = super.onCreateView(inflater, container, savedInstanceState);
        mapView = layoutDataBinding.mapViewDestination;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        destinationArea = layoutDataBinding.dailySearch;
        destinationArea.setOnQueryTextListener(this);
        fab = layoutDataBinding.addDestination;
        return view;
    }

    @SuppressLint("WrongConstant")
    private void enableLocation(Style loadedMapStyle){

        if(PermissionsManager.areLocationPermissionsGranted(getActivity())){
            LocationComponentOptions.builder(getActivity()).build();
            locationComponent = map.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getActivity(), map.getStyle()).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);

        } else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }
    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation(map.getStyle());
        }
        else
        {
            Toast.makeText(getActivity(),"Permissions not granted",Toast.LENGTH_SHORT).show();
            //finish();
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

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(),"Location needed to route",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        if(marker != null)
        {
            map.removeMarker(marker);
        }
        marker = map.addMarker(new MarkerOptions().position(point));
        if(destinationIndicator)
            dailyDestination = Point.fromLngLat(point.getLongitude(),point.getLatitude());
        else
            dailySource = Point.fromLngLat(point.getLongitude(),point.getLatitude());
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        final String name = query;
        Geocoder coder = new Geocoder(getActivity());
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
                Toast.makeText(getActivity(), "No country found", Toast.LENGTH_SHORT).show();
                return false;
                //query = query + ","+locationComponent.getLastKnownLocation().getLongitude()+","+locationComponent.getLastKnownLocation().getLatitude();
            }
            final String regionName = query;
            Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();
//        if(isInternetAvailable()){


            address = coder.getFromLocationName(query, 5);
            if (address == null) {
                Toast.makeText(getActivity(), "No place found", Toast.LENGTH_SHORT).show();
                return false;
            }
            Address loc = address.get(0);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 13.0));
            destinationArea.clearFocus();
            Toast.makeText(getActivity(), "Mark location on map", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getActivity(), "Inside catch", Toast.LENGTH_SHORT).show();
            // Get the region bounds and zoom and move the camera.
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        //unregisterReceiver(MyReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
