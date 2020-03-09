package com.tcd.yaatra.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tcd.yaatra.R;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.databinding.ActivityPeerToPeerBinding;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.repository.models.FellowTravellersCache;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;
import com.tcd.yaatra.ui.adapter.PeerListAdapter;
import com.tcd.yaatra.utils.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

public class PeerToPeerActivity extends BaseActivity<ActivityPeerToPeerBinding> {


    @Inject
    UserInfoRepository userInfoRepository;
    PeerCommunicator communicator;
    private ArrayList<TravellerInfo> travellerInfos = new ArrayList<>();
    private Bundle bundle;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    boolean isLocationPermissionGranted = false;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    int getLayoutResourceId() {
        return R.layout.activity_peer_to_peer;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.peerRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutDataBinding.peerRecyclerView.setLayoutManager(layoutManager);
        refreshRecyclerView();
        layoutDataBinding.startNavigation.setOnClickListener(view -> handleStartNavigationClick());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this.travellerInfos = PeerTravellerInfoMocks.getPeerTravellerList();  // for mock purpose
        super.onCreate(savedInstanceState);
        this.bundle = getIntent().getExtras();
        FellowTravellersCache.getCacheInstance().clear();
        enableWiFi();
        initializePeerCommunicator();
        checkIfLocationPermissionGranted();
    }

    private void enableWiFi(){
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
    }

    private void initializePeerCommunicator(){
        communicator = new PeerCommunicator(this, userInfoRepository, bundle);
    }

    private void checkIfLocationPermissionGranted(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            isLocationPermissionGranted = false;

            //if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this
                    , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}
                    , LOCATION_PERMISSION_REQUEST_CODE);
            //}
        }
        else {
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
                }
                return;
            }
        }
    }

    //endregion

    //region Activity Life Cycle

    @Override
    protected void onPause() {
//        travellerInfos.clear();
        refreshRecyclerView();

        if(communicator != null){
            communicator.cleanup();
            communicator = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePeerCommunicator();
        communicator.registerPeerActivityListener();
    }

    @Override
    protected void onDestroy() {
//        travellerInfos.clear();
        refreshRecyclerView();

        if(communicator != null) {
            communicator.cleanup();
            communicator = null;
        }
        super.onDestroy();
    }

    //endregion

    public void startDiscovery(){
        if(isLocationPermissionGranted){
            startLoadingAnimation();
            communicator.advertiseStatusAndDiscoverFellowTravellers(TravellerStatus.SeekingFellowTraveller);
        }
    }

    private void startLoadingAnimation(){
        TashieLoader tashie = new TashieLoader(
                this, 5,
                30, 10,
                ContextCompat.getColor(this, R.color.colorAccent));

        tashie.setAnimDuration(500);
        tashie.setAnimDelay(100);
        tashie.setInterpolator(new LinearInterpolator());

        layoutDataBinding.loader.addView(tashie);
    }

    private void handleStartNavigationClick(){
        Intent mapIntent = new Intent(PeerToPeerActivity.this, RouteInfo.class);
        Bundle bundle = getIntent().getExtras();

        //rohan+chetan: Mocking multi destination start
        ArrayList<LatLng> locations = new ArrayList<>();
        Boolean multidestination = false;
        LatLng point1 = new LatLng(53.34684951262867,-6.259117126464844);
        LatLng point2 = new LatLng(53.34587597399326,-6.255190372467041);
        LatLng point3 = new LatLng(53.345145805428125,-6.254847049713134);
        LatLng point4 = new LatLng(53.34490241312763,-6.253151893615723);
        locations.add(point1);locations.add(point2); locations.add(point3);locations.add(point4);
        bundle.putParcelableArrayList("destLocations", locations);
        bundle.putBoolean("multiDestination", multidestination);
        //rohan+chetan: Mocking multi destination end
        mapIntent.putExtras(bundle);
        startActivity(mapIntent);
    }

    public void showFellowTravellers(HashMap<Integer, TravellerInfo> peerTravellers, TravellerInfo ownTravellerInfo){
        this.travellerInfos.clear();
        ArrayList<TravellerInfo> peerTravellerArrayList = new ArrayList<>(peerTravellers.values());
        MapUtils.filterFellowTravellers(ownTravellerInfo, peerTravellerArrayList).forEach(travellerInfo -> this.travellerInfos.add(travellerInfo));
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        mAdapter = new PeerListAdapter(getApplicationContext(), this.travellerInfos);
        layoutDataBinding.peerRecyclerView.setAdapter(mAdapter);
    }
}