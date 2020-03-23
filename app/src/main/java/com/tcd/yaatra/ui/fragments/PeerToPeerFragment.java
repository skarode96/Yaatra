package com.tcd.yaatra.ui.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.gson.Gson;
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
import com.tcd.yaatra.ui.adapter.PeerListAdapter;
import com.tcd.yaatra.utils.MapUtils;
import com.tcd.yaatra.utils.NetworkUtils;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class PeerToPeerFragment extends FellowTravellersSubscriberFragment<FragmentPeerToPeerBinding> {

    //region Private Variables

    @Inject
    UserInfoRepository userInfoRepository;

    @Inject
    PeerCommunicator peerCommunicator;

    @Inject
    TravellerInfo ownTravellerInfo;

    @Inject
    FellowTravellersCache fellowTravellersCache;

    private ArrayList<TravellerInfo> travellerInfos = new ArrayList<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String COUNTDOWN_FORMAT = "%02d:%02d";
    private static final int SCAN_DURATION_IN_MILLISECONDS = 60000;
    private static final int COUNTDOWN_INTERVAL_IN_MILLISECONDS  = 1000;
    private static final String MEET_AT = "Let's Meet Others!";
    private static final String WAIT_FOR_OTHERS = "You are the leader. Please wait for others!";
    private static final String LISTENING_TO_FELLOW_TRAVELLERS = "Listening to Fellow Travellers";
    private static final String ENJOY_YOUR_OWN_COMPANY = "Enjoy your own company!! :)";
    private boolean isLocationPermissionGranted = false;
    private boolean isUserInfoFetched = false;
    private static final String TAG = "PeerToPeerFragment";
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    WifiManager wifiManager;
    private Handler backgroundTaskHandler;
    PeerToPeerFragment fragmentContext;
    View view;
    private ArrayList<LatLng> travelPath;

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
        layoutDataBinding.startNavigation.setOnClickListener(view -> handleStartNavigationClick());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = super.onCreateView(inflater, container, savedInstanceState);
        initialize();

        return view;
    }

    private void initialize() {
        fragmentContext = this;
        backgroundTaskHandler = new Handler();

        layoutDataBinding.initializeProgressBar.setVisibility(View.VISIBLE);
        layoutDataBinding.startNavigation.hide();
        layoutDataBinding.gridLoader.setVisibility(View.INVISIBLE);

        wifiManager = (WifiManager) this.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        askLocationPermissionIfRequired();

        isUserInfoFetched = false;

        userInfoRepository.getUserProfile(SharedPreferenceUtils.getUserName()).observe(this.getActivity(), userInfo -> {

            insertOwnTravellerInfo(userInfo);
        });

        backgroundInitializer.run();
    }

    private Runnable backgroundInitializer = new Runnable() {
        @Override
        public void run() {

            try {

                if (wifiManager.isWifiEnabled() && isUserInfoFetched && isLocationPermissionGranted) {

                    layoutDataBinding.initializeProgressBar.setVisibility(View.GONE);

                    peerCommunicator.initialize(fragmentContext);

                    peerCommunicator.broadcastTravellers(TravellerStatus.SeekingFellowTraveller);

                    if(travellerInfos.size()>0){
                        instructUser();
                    }
                    else {
                        layoutDataBinding.tvSearching.setText(getResources().getString(R.string.searching_label));
                        startCountdown();
                    }

                    layoutDataBinding.gridLoader.setVisibility(View.VISIBLE);
                } else {
                    backgroundTaskHandler
                            .postDelayed(backgroundInitializer, 200);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error: " + ex.getMessage(), ex);
            }
        }
    };

    private void insertOwnTravellerInfo(UserInfo userInfo) {

        LocalDateTime now = LocalDateTime.now();

        ownTravellerInfo.setUserId(userInfo.getId());
        ownTravellerInfo.setUserName(userInfo.getUsername());
        ownTravellerInfo.setAge(userInfo.getAge());
        ownTravellerInfo.setGender(Gender.valueOfIdName(userInfo.getGender()));
        ownTravellerInfo.setUserRating(userInfo.getRating());
        ownTravellerInfo.setIpAddress(NetworkUtils.getWiFiIPAddress(this.getActivity()));
        ownTravellerInfo.setRequestStartTime(now);
        ownTravellerInfo.setStatusUpdateTime(now);
        ownTravellerInfo.setInfoProvider(userInfo.getUsername());

        processFellowTravellersInfo(fellowTravellersCache.getFellowTravellers());

        isUserInfoFetched = true;
    }

    private void startCountdown() {
        new CountDownTimer(SCAN_DURATION_IN_MILLISECONDS, COUNTDOWN_INTERVAL_IN_MILLISECONDS) {

            public void onTick(long millisUntilFinished) {
                String formattedTimerText = String.format(COUNTDOWN_FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)%60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);

                layoutDataBinding.tvInstructions.setText(formattedTimerText);
            }

            public void onFinish() {

                fellowTravellersCache.stopNewInsert();

                instructUser();
            }
        }.start();
    }

    private void instructUser() {
        if(travellerInfos.size() > 0 && amIGroupOwner()){
            layoutDataBinding.tvInstructions.setTextSize(20);
            layoutDataBinding.tvInstructions.setText(WAIT_FOR_OTHERS);
            layoutDataBinding.tvSearching.setText(LISTENING_TO_FELLOW_TRAVELLERS);
            enableStartNavigationButton();
        }
        else if(travellerInfos.size() > 0){
            layoutDataBinding.tvInstructions.setText(MEET_AT);
            layoutDataBinding.tvSearching.setText(LISTENING_TO_FELLOW_TRAVELLERS);
            enableStartNavigationButton();
        }
        else {
            layoutDataBinding.tvInstructions.setText(ENJOY_YOUR_OWN_COMPANY);
            layoutDataBinding.cvSoloTravel.setVisibility(View.VISIBLE);
        }
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

        peerCommunicator.broadcastTravellers(TravellerStatus.TravellingToStartPoint);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("destLocations", travelPath);

        Gson gson = new Gson();
        bundle.putString("UserList", gson.toJson(travellerInfos));
        bundle.putBoolean("multiDestination", true);

        RouteInfoFragment routeInfoFragment = new RouteInfoFragment();
        routeInfoFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, routeInfoFragment).addToBackStack("peerFrag").commit();
    }

    //endregion

    @Override
    protected void processFellowTravellersInfo(HashMap<Integer, TravellerInfo> fellowTravellers) {

        this.travellerInfos.clear();
        ArrayList<TravellerInfo> peerTravellerArrayList = new ArrayList<>(fellowTravellers.values());
        MapUtils.filterFellowTravellers(ownTravellerInfo, peerTravellerArrayList).forEach(travellerInfo -> this.travellerInfos.add(travellerInfo));
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        mAdapter = new PeerListAdapter(this.getActivity().getApplicationContext(), this.travellerInfos);
        layoutDataBinding.peerRecyclerView.setAdapter(mAdapter);
    }

    private boolean amIGroupOwner(){
        return makeTravelPath();
    }

    //check if current user is group owner of the travellers list
    //Initialize travel path including destinations of all travellers
    //Travel path also includes group owner's location as destination for other travellers
    private boolean makeTravelPath() {

        travelPath = new ArrayList<>();
        LocalDateTime leastFellowTravellerRequestStartTime = LocalDateTime.MAX;

        double groupOwnerSourceLat = 0.0;
        double groupOwnerSourceLong = 0.0;

        Iterator<TravellerInfo> iterator = travellerInfos.iterator();

        while(iterator.hasNext()){

            TravellerInfo info = iterator.next();

            if(info.getRequestStartTime().isBefore(leastFellowTravellerRequestStartTime)){
                leastFellowTravellerRequestStartTime = info.getRequestStartTime();
                groupOwnerSourceLat = info.getSourceLatitude();
                groupOwnerSourceLong = info.getSourceLongitude();
            }

            travelPath.add(new LatLng(info.getDestinationLatitude(), info.getDestinationLongitude()));
        }

        boolean isGroupOwner = ownTravellerInfo.getRequestStartTime().isBefore(leastFellowTravellerRequestStartTime);

        if(!isGroupOwner){
            travelPath.add(0, new LatLng(groupOwnerSourceLat, groupOwnerSourceLong));
        }

        travelPath.add(0, new LatLng(ownTravellerInfo.getSourceLatitude(), ownTravellerInfo.getSourceLongitude()));
        travelPath.add(new LatLng(ownTravellerInfo.getDestinationLatitude(), ownTravellerInfo.getDestinationLongitude()));

        return isGroupOwner;
    }

    private void enableStartNavigationButton(){
        if (travellerInfos.size() > 0) {
            layoutDataBinding.startNavigation.show();
        } else {
            layoutDataBinding.startNavigation.hide();
        }
    }
}