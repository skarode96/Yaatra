package com.tcd.yaatra.ui.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityMapBinding;
import com.tcd.yaatra.databinding.ActivityRouteinfoBinding;
import com.tcd.yaatra.ui.viewmodels.MapActivityViewModel;

import androidx.appcompat.app.AlertDialog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteInfo extends BaseActivity<ActivityRouteinfoBinding> implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, OnNavigationReadyCallback, NavigationListener, RouteListener, ProgressChangeListener{

    private MapView mapView;
    private MapboxMap map;
    LocationComponent locationComponent;
    private Button startButton;
    private PermissionsManager permissionsManager;
    private Point originPosition;
    private Point destinationPosition;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "MainActivity";
    private DirectionsRoute currentGivenRoute;
    private NavigationView navigationView;
    private boolean dropoffDialogShown;
    private Location lastKnownLocation;

    @Inject
    com.tcd.yaatra.ui.viewmodels.MapActivityViewModel MapActivityViewModel;
    //SharedPreferences loginPreferences;

    @Override
    int getLayoutResourceId() { return R.layout.activity_routeinfo; }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.startButton.setOnClickListener(view -> handleOnStartButtonClick());
    }

    private void handleOnStartButtonClick() {/*
        NavigationViewOptions options = NavigationViewOptions.builder()
                        .directionsRoute(currentGivenRoute)
                        .navigationListener(this)
                        .progressChangeListener(this)
                        .routeListener(this)
                        .shouldSimulateRoute(true)
                        .build();

        NavigationLauncher.startNavigation(RouteInfo.this,setupOptions(currentGivenRoute));
        startNavigation(currentGivenRoute);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.access_token));
        setContentView(R.layout.activity_routeinfo);
        mapView = layoutDataBinding.mapView;
        startButton = layoutDataBinding.startButton;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }


    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),13.0));
    }

    public void showRoute() {

        Bundle bundle = getIntent().getExtras();
        double latitude = bundle.getDouble("Latitude");
        double longitude = bundle.getDouble("Longitude");
        destinationPosition = Point.fromLngLat(longitude,latitude);
        originPosition = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),locationComponent.getLastKnownLocation().getLatitude());
        getRoute(originPosition,destinationPosition);

        startButton.setEnabled(true);
        startButton.setBackgroundResource(R.color.mapbox_blue);
    }

    private void getRoute(Point origin, Point destination){
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body() == null){
                            Log.e(TAG,"No routes found, check right user and access token");
                            return;
                        } else if(response.body().routes().size()==0){
                            Log.e(TAG,"No routes found");
                            return;
                        }

                        DirectionsRoute currentRoute = response.body().routes().get(0);
                        currentGivenRoute = currentRoute;

                        if(navigationMapRoute != null){
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null,mapView,map);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG,"Error:"+t.getMessage());
                    }
                });
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this,"Location needed to route",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {

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

        return false;
    }


    @Override
    public void onNavigationReady(boolean isRunning) {
        showRoute();
    }

    @Override
    public void onCancelNavigation() {
        // Navigation canceled, finish the activity
        finish();
    }

    @Override
    public void onNavigationFinished() {
        // Intentionally empty
    }

    @Override
    public void onNavigationRunning() {
        // Intentionally empty
    }

    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        return true;
    }

    @Override
    public void onOffRoute(Point offRoutePoint) {

    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }

    @Override
    public void onArrival() {
        if (destinationPosition != null) {
            Toast.makeText(this, "You have arrived!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        lastKnownLocation = location;
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions navigationViewOptions = setupOptions(directionsRoute);
    }



    private NavigationViewOptions setupOptions(DirectionsRoute directionsRoute) {
        dropoffDialogShown = false;

        NavigationViewOptions.Builder options = NavigationViewOptions.builder();
        options.directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this)
                .shouldSimulateRoute(true);
        return options.build();
    }

    private Point getLastKnownLocation() {
        return Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

    }
}