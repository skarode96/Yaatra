package com.tcd.yaatra.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityRouteinfoBinding;
import com.tcd.yaatra.ui.viewmodels.RouteInfoViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
public class RouteInfo extends BaseActivity<ActivityRouteinfoBinding> implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, OnNavigationReadyCallback, NavigationListener, RouteListener, ProgressChangeListener{

    private MapView mapView;
    private MapboxMap map;
    LocationComponent locationComponent;
    private Button startButton;
    private Button endTrip;
    private PermissionsManager permissionsManager;
    private Point originPosition;
    private Point destinationPosition;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "MainActivity";
    private DirectionsRoute currentGivenRoute;
    private NavigationView navigationView;
    private boolean dropoffDialogShown;
    private Location lastKnownLocation;
    private static final String ICON_GEOJSON_SOURCE_ID = "icon-source-id";
    private Point origin;
    private static final String TEAL_COLOR = "#23D2BE";
    private static final float POLYLINE_WIDTH = 5;
    private List<Point> stops = new ArrayList<>();
    @Inject
    RouteInfoViewModel RouteInfoViewModel;

    @Override
    protected int getLayoutResourceId() { return R.layout.activity_routeinfo; }

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.startButton.setOnClickListener(view -> handleOnStartButtonClick());
        layoutDataBinding.endNavigation.setOnClickListener(view -> handleOnendNavigationClick());
    }

    private void handleOnendNavigationClick() {
        Intent userrate = new Intent(this, UserRatingActivity.class);
        startActivity(userrate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this,getString(R.string.access_token));
        super.onCreate(savedInstanceState);
        mapView = layoutDataBinding.mapView;
        startButton = layoutDataBinding.startButton;
        endTrip = layoutDataBinding.endNavigation;
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void handleOnStartButtonClick() {

        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentGivenRoute)
                .shouldSimulateRoute(true)
                .build();

        NavigationLauncher.startNavigation(RouteInfo.this,options);
        startButton.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                endTrip.setVisibility(View.VISIBLE);
            }
        }, 5000);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.map = mapboxMap;
//        map.setMinZoomPreference(15);
//        map.setStyle(Style.MAPBOX_STREETS);
        Bundle bundle = getIntent().getExtras();
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                if(bundle.getBoolean("multiDestination")) {
                    initMarkerIconSymbolLayer(style);
                    initOptimizedRouteLineLayer(style);
                }
                enableLocation(style);
            }
        });

    }

    private void initMarkerIconSymbolLayer(@NonNull Style loadedMapStyle) {
        // Add the marker image to map
        loadedMapStyle.addImage("icon-image", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.marker_travel));

        // Add the source to the map
        Bundle bundle = getIntent().getExtras();
        ArrayList<LatLng> locations  = bundle.getParcelableArrayList("destLocations");
        addFirstStopToStopsList(locations.get(0));
        loadedMapStyle.addSource(new GeoJsonSource(ICON_GEOJSON_SOURCE_ID,
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude()))));

        loadedMapStyle.addLayer(new SymbolLayer("icon-layer-id", ICON_GEOJSON_SOURCE_ID).withProperties(
                iconImage("icon-image"),
                iconSize(1f),
                iconAllowOverlap(true),
                iconIgnorePlacement(true),
                iconOffset(new Float[] {0f, -7f})
        ));
    }

    private void initOptimizedRouteLineLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource("optimized-route-source-id"));
        loadedMapStyle.addLayerBelow(new LineLayer("optimized-route-layer-id", "optimized-route-source-id")
                .withProperties(
                        lineColor(Color.parseColor(TEAL_COLOR)),
                        lineWidth(POLYLINE_WIDTH)
                ), "icon-layer-id");
    }

    private void addDestinationMarker(@NonNull Style style, LatLng point) {
        List<Feature> destinationMarkerList = new ArrayList<>();
        for (Point singlePoint : stops) {
            destinationMarkerList.add(Feature.fromGeometry(
                    Point.fromLngLat(singlePoint.longitude(), singlePoint.latitude())));
        }
        destinationMarkerList.add(Feature.fromGeometry(Point.fromLngLat(point.getLongitude(), point.getLatitude())));
        GeoJsonSource iconSource = style.getSourceAs(ICON_GEOJSON_SOURCE_ID);
        if (iconSource != null) {
            iconSource.setGeoJson(FeatureCollection.fromFeatures(destinationMarkerList));
        }
    }

    private void addFirstStopToStopsList(LatLng startPoint) {
        origin = Point.fromLngLat(startPoint.getLongitude(), startPoint.getLatitude());
        stops.add(origin);
    }
    @SuppressLint("WrongConstant")
    private void enableLocation(Style loadedMapStyle){

        if(PermissionsManager.areLocationPermissionsGranted(this)){
            LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(this).build();
            locationComponent = map.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, map.getStyle()).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            showRoute();
        } else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),13.0));
    }

    public void showRoute() {

        Bundle bundle = getIntent().getExtras();
        double latitude = bundle.getDouble("destinationLatitude");
        double longitude = bundle.getDouble("destinationLongitude");
        String modeOfTravel =  bundle.getString("peerModeOfTravel");
        Boolean multiDestination = bundle.getBoolean("multiDestination");
        if(multiDestination){
            ArrayList<LatLng> locations  = bundle.getParcelableArrayList("destLocations");
            Style style = map.getStyle();
            for(int i = 1; i< locations.size(); i++)
            {
                addDestinationMarker(style, locations.get(i));
                addPointToStopsList(locations.get(i));
            }
            getOptimizedRoute(modeOfTravel);
        }
        else {

            destinationPosition = Point.fromLngLat(longitude, latitude);
            originPosition = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(), locationComponent.getLastKnownLocation().getLatitude());
            getRoute(originPosition, destinationPosition, modeOfTravel);
        }
        startButton.setEnabled(true);
        startButton.setBackgroundResource(R.color.mapbox_blue);
    }

    private void addPointToStopsList(LatLng point) {
        stops.add(Point.fromLngLat(point.getLongitude(), point.getLatitude()));
    }
    private void getRoute(Point origin, Point destination, String modeOfTravel){
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(modeOfTravel)
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

    private void getOptimizedRoute(String modeOfTravel){

        NavigationRoute.Builder builder = NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(stops.get(stops.size()-1))
                .profile(modeOfTravel);

        for(int i = 1; i < stops.size() -1 ; i++)
        {
            builder.addWaypoint(stops.get(i));
        }

        builder.build().getRoute(new Callback<DirectionsResponse>() {
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
        Toast.makeText(this,"End of trip!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNavigationFinished() {
        // Intentionally empty
        Toast.makeText(this,"End of trip!",Toast.LENGTH_SHORT).show();
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

}