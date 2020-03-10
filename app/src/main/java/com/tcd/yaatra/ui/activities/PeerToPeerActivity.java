package com.tcd.yaatra.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tcd.yaatra.R;
import com.tcd.yaatra.WifiDirectP2PHelper.FellowTravellersSubscriberActivity;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.repository.models.FellowTravellersCache;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;
import com.tcd.yaatra.ui.adapter.PeerListAdapter;
import com.tcd.yaatra.utils.MapUtils;
import com.tcd.yaatra.utils.NetworkUtils;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;

public class PeerToPeerActivity extends FellowTravellersSubscriberActivity {

    //region Private Variables

    @Inject
    UserInfoRepository userInfoRepository;
    PeerCommunicator communicator;
    private ArrayList<TravellerInfo> travellerInfos = new ArrayList<>();
    private Bundle bundle;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean isLocationPermissionGranted = false;
    private boolean isUserInfoFetched = false;
    private static final String TAG = "PeerToPeerActivity";
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TravellerInfo ownTravellerInfo;
    WifiManager wifiManager;
    private Handler backgroundTaskHandler;
    PeerToPeerActivity activityContext;

    //endregion

    //region Initialization

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_peer_to_peer;
    }

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.peerRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutDataBinding.peerRecyclerView.setLayoutManager(layoutManager);
        refreshRecyclerView();
        layoutDataBinding.startNavigation.setOnClickListener(view -> handleStartNavigationClick());
        layoutDataBinding.goToStart.setOnClickListener(view -> handleGoToStartClick());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.bundle = getIntent().getExtras();
        activityContext = this;

        initialize();
    }

    private void initialize() {
        backgroundTaskHandler = new Handler();

        layoutDataBinding.initializeProgressBar.setVisibility(View.VISIBLE);

        travellerInfos.clear();
        FellowTravellersCache.getCacheInstance().clear();

        layoutDataBinding.startNavigation.hide();
        layoutDataBinding.goToStart.hide();
        layoutDataBinding.gridLoader.setVisibility(View.INVISIBLE);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        askLocationPermissionIfRequired();

        userInfoRepository.getUserProfile(SharedPreferenceUtils.getUserName()).observe(this, userInfo -> {

            createOwnTravellerInfo(userInfo);
        });

        backgroundInitializer.run();
    }

    private Runnable backgroundInitializer = new Runnable() {
        @Override
        public void run() {

            try {

                if (wifiManager.isWifiEnabled() && isUserInfoFetched && isLocationPermissionGranted) {

                    layoutDataBinding.initializeProgressBar.setVisibility(View.GONE);

                    communicator = new PeerCommunicator(activityContext, ownTravellerInfo);

                    broadcastTravellers(TravellerStatus.SeekingFellowTraveller);

                    layoutDataBinding.gridLoader.setVisibility(View.VISIBLE);
                } else {
                    backgroundTaskHandler
                            .postDelayed(backgroundInitializer, 200);
                }
            }catch (Exception ex){
                Log.e(TAG, "Error: " + ex.getMessage(), ex);
            }
        }
    };

    private void createOwnTravellerInfo(UserInfo userInfo) {

        LocalDateTime now = LocalDateTime.now();
        double sourceLatitude = this.bundle.getDouble("sourceLatitude");
        double sourceLongitude = this.bundle.getDouble("sourceLongitude");
        double destinationLatitude = this.bundle.getDouble("destinationLatitude");
        double destinationLongitude = this.bundle.getDouble("destinationLongitude");
        String sourceName = this.bundle.getString("sourceName").substring(0, 10);
        String destinationName = this.bundle.getString("destinationName").substring(0, 10);
        String modeOfTravel = this.bundle.getString("peerModeOfTravel");

        ownTravellerInfo =
                new TravellerInfo(userInfo.getId(), userInfo.getUsername(), userInfo.getAge(), Gender.valueOfIdName(userInfo.getGender())
                        , sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude
                        , TravellerStatus.None, sourceName, destinationName, modeOfTravel, now, userInfo.getRating()
                        , NetworkUtils.getWiFiIPAddress(this)
                        , 12345, now, userInfo.getUsername());

        isUserInfoFetched = true;
    }

    private void askLocationPermissionIfRequired() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            isLocationPermissionGranted = false;

            ActivityCompat.requestPermissions(this
                    , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}
                    , LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            isLocationPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionGranted = true;
                } else {
                    isLocationPermissionGranted = false;
                    Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG);
                }
            }
        }
    }

    //endregion

    //region Activity Life Cycle

    @Override
    protected void onPause() {

        refreshRecyclerView();

        if (communicator != null) {
            communicator.cleanup();
            communicator = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        refreshRecyclerView();

        if (communicator != null) {
            communicator.cleanup();
            communicator = null;
        }
        super.onDestroy();
    }

    //endregion

    public void broadcastTravellers(TravellerStatus ownStatus) {

        setCurrentStatusOfAppUser(ownStatus);
        communicator.updateOwnTraveller(ownTravellerInfo);

        if (isLocationPermissionGranted) {
            communicator.advertiseStatusAndDiscoverFellowTravellers();
        }
    }

    private void setCurrentStatusOfAppUser(TravellerStatus status) {
        if (ownTravellerInfo.getStatus() != status) {
            ownTravellerInfo.setStatusUpdateTime(LocalDateTime.now());
        }

        ownTravellerInfo.setStatus(status);
    }

    //region Click Handlers

    private void handleStartNavigationClick() {
        Intent mapIntent = new Intent(PeerToPeerActivity.this, RouteInfo.class);
        Bundle bundle = getIntent().getExtras();

        //rohan+chetan: Mocking multi destination start
        ArrayList<LatLng> locations = new ArrayList<>();
        Boolean multidestination = true;
        LatLng point1 = new LatLng(53.34684951262867, -6.259117126464844);
        LatLng point2 = new LatLng(53.34587597399326, -6.255190372467041);
        LatLng point3 = new LatLng(53.345145805428125, -6.254847049713134);
        LatLng point4 = new LatLng(53.34490241312763, -6.253151893615723);
        locations.add(point1);
        locations.add(point2);
        locations.add(point3);
        locations.add(point4);
        bundle.putParcelableArrayList("destLocations", locations);
        bundle.putBoolean("multiDestination", multidestination);
        //rohan+chetan: Mocking multi destination end
        mapIntent.putExtras(bundle);
        startActivity(mapIntent);
    }

    private void handleGoToStartClick() {
        broadcastTravellers(TravellerStatus.TravellingToStartPoint);
    }

    //endregion

    @Override
    protected void processFellowTravellersInfo(HashMap<Integer, TravellerInfo> fellowTravellers) {

        this.travellerInfos.clear();
        ArrayList<TravellerInfo> peerTravellerArrayList = new ArrayList<>(fellowTravellers.values());
        MapUtils.filterFellowTravellers(ownTravellerInfo, peerTravellerArrayList).forEach(travellerInfo -> this.travellerInfos.add(travellerInfo));
        refreshRecyclerView();

        if (travellerInfos.size() > 0) {
            layoutDataBinding.goToStart.show();
        } else {
            layoutDataBinding.goToStart.hide();
        }
    }

    private void refreshRecyclerView() {
        mAdapter = new PeerListAdapter(getApplicationContext(), this.travellerInfos);
        layoutDataBinding.peerRecyclerView.setAdapter(mAdapter);
    }
}