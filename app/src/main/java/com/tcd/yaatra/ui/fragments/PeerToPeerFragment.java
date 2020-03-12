package com.tcd.yaatra.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tcd.yaatra.R;
import com.tcd.yaatra.WifiDirectP2PHelper.FellowTravellersSubscriberFragment;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.databinding.FragmentPeerToPeerBinding;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.repository.models.FellowTravellersCache;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;
import com.tcd.yaatra.ui.activities.RouteInfo;
import com.tcd.yaatra.ui.adapter.PeerListAdapter;
import com.tcd.yaatra.utils.MapUtils;
import com.tcd.yaatra.utils.NetworkUtils;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;

public class PeerToPeerFragment extends FellowTravellersSubscriberFragment<FragmentPeerToPeerBinding> {

    //region Private Variables

    @Inject
    UserInfoRepository userInfoRepository;

    @Inject
    PeerCommunicator peerCommunicator;

    private ArrayList<TravellerInfo> travellerInfos = new ArrayList<>();
    private Bundle bundle;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean isLocationPermissionGranted = false;
    private boolean isUserInfoFetched = false;
    private static final String TAG = "PeerToPeerFragment";
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TravellerInfo ownTravellerInfo;
    WifiManager wifiManager;
    private Handler backgroundTaskHandler;
    PeerToPeerFragment fragmentContext;
    View view;

    //endregion

    //region Initialization

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_peer_to_peer;
    }

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.peerRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getActivity());
        layoutDataBinding.peerRecyclerView.setLayoutManager(layoutManager);
        refreshRecyclerView();
        layoutDataBinding.startNavigation.setOnClickListener(view -> handleStartNavigationClick());
        layoutDataBinding.fabGoToStart.setOnClickListener(view -> handleGoToStartClick());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = super.onCreateView(inflater, container, savedInstanceState);

        this.bundle = getArguments();
        initialize();

        return view;
    }

    private void initialize() {
        fragmentContext = this;
        backgroundTaskHandler = new Handler();

        layoutDataBinding.initializeProgressBar.setVisibility(View.VISIBLE);

        travellerInfos.clear();
        FellowTravellersCache.getCacheInstance().clear();
        refreshRecyclerView();

        layoutDataBinding.startNavigation.hide();
        layoutDataBinding.fabGoToStart.hide();
        layoutDataBinding.gridLoader.setVisibility(View.INVISIBLE);

        wifiManager = (WifiManager) this.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        askLocationPermissionIfRequired();

        userInfoRepository.getUserProfile(SharedPreferenceUtils.getUserName()).observe(this.getActivity(), userInfo -> {

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

                    peerCommunicator.initialize(fragmentContext, ownTravellerInfo);

                    peerCommunicator.broadcastTravellers(TravellerStatus.SeekingFellowTraveller);

                    layoutDataBinding.gridLoader.setVisibility(View.VISIBLE);

                    //test();
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
                        , NetworkUtils.getWiFiIPAddress(this.getActivity())
                        , 12345, now, userInfo.getUsername());

        isUserInfoFetched = true;
    }

    private void askLocationPermissionIfRequired() {
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            isLocationPermissionGranted = false;

            ActivityCompat.requestPermissions(this.getActivity()
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
                    Toast.makeText(this.getActivity(), "Location permission is required", Toast.LENGTH_LONG);
                }
            }
        }
    }

    //endregion

    //region Activity Life Cycle

    @Override
    public void onPause() {

        travellerInfos.clear();
        refreshRecyclerView();
        super.onPause();
    }

    @Override
    public void onDestroy() {

        travellerInfos.clear();
        refreshRecyclerView();
        super.onDestroy();
    }

    //endregion

    //region Click Handlers

    private void handleStartNavigationClick() {
        Intent mapIntent = new Intent(this.getActivity(), RouteInfo.class);

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
        peerCommunicator.broadcastTravellers(TravellerStatus.TravellingToStartPoint);
    }

    //endregion

    @Override
    protected void processFellowTravellersInfo(HashMap<Integer, TravellerInfo> fellowTravellers) {

        this.travellerInfos.clear();
        ArrayList<TravellerInfo> peerTravellerArrayList = new ArrayList<>(fellowTravellers.values());
        MapUtils.filterFellowTravellers(ownTravellerInfo, peerTravellerArrayList).forEach(travellerInfo -> this.travellerInfos.add(travellerInfo));
        refreshRecyclerView();

        if (travellerInfos.size() > 0) {
            layoutDataBinding.fabGoToStart.show();
        } else {
            layoutDataBinding.fabGoToStart.hide();
        }
    }

    private void refreshRecyclerView() {
        mAdapter = new PeerListAdapter(this.getActivity().getApplicationContext(), this.travellerInfos);
        layoutDataBinding.peerRecyclerView.setAdapter(mAdapter);
    }

    private void test(){

        TravellerInfo info = ownTravellerInfo.clone();

        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();

        for(int i=2; i<9; i++){
            info.setUserId(i);
            info.setUserName("JP");
            fellowTravellers.put(i, info);
        }

        processFellowTravellersInfo(fellowTravellers);

    }
}