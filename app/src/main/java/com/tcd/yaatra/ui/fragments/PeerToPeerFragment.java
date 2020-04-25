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
import com.tcd.yaatra.R;
import com.tcd.yaatra.WifiDirectP2PHelper.FellowTravellersSubscriberFragment;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.databinding.FragmentPeerToPeerBinding;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.repository.models.TravellerStatus;
import com.tcd.yaatra.ui.adapters.PeerListAdapter;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;
import com.tcd.yaatra.utils.Constants;
import com.tcd.yaatra.utils.NetworkUtils;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class PeerToPeerFragment extends FellowTravellersSubscriberFragment<FragmentPeerToPeerBinding> {

    //region Private Variables

    @Inject
    protected UserInfoRepository userInfoRepository;

    @Inject
    protected PeerCommunicator peerCommunicator;

    @Inject
    protected PeerToPeerFragmentViewModel peerToPeerFragmentViewModel;

    private boolean isLocationPermissionGranted = false;
    private TravellerStatus ownStatus = TravellerStatus.SeekingFellowTraveller;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
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
        layoutDataBinding.startNavigation.setOnClickListener(view -> handleStartNavigationClick());

        layoutDataBinding.travllerStatusToggle.setVisibility(View.INVISIBLE);
        //layoutDataBinding.travllerStatusToggle.setOnClickListener(view -> handleStatusUpdateClick());
    }

    private void handleStatusUpdateClick() {
        this.ownStatus = layoutDataBinding.travllerStatusToggle.getText() == "Seeking" ? TravellerStatus.SeekingFellowTraveller : TravellerStatus.ReachedStartPoint;
        peerCommunicator.broadcastTravellers(getOwnStatus());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = super.onCreateView(inflater, container, savedInstanceState);
        initialize();

        return view;
    }

    protected void initialize() {
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

        peerToPeerFragmentViewModel.setIsInitialized(false);

        userInfoRepository.getUserProfile(SharedPreferenceUtils.getUserName()).observe(this.getActivity(), userInfo -> {

            peerToPeerFragmentViewModel.initializeOwnTraveller(userInfo, NetworkUtils.getWiFiIPAddress(this.getActivity()));
        });

        backgroundInitializer.run();
    }

    private Runnable backgroundInitializer = new Runnable() {
        @Override
        public void run() {

            try {

                if (wifiManager.isWifiEnabled() && isLocationPermissionGranted && peerToPeerFragmentViewModel.getIsInitialized()) {

                    layoutDataBinding.initializeProgressBar.setVisibility(View.GONE);

                    peerCommunicator.initialize(fragmentContext);

                    peerCommunicator.broadcastTravellers(getOwnStatus());

                    if(peerToPeerFragmentViewModel.getFilteredPeerTravellers().size()>0){
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
                Log.e(Constants.TAG_PEER_TO_PEER_FRAGMENT, "Error: " + ex.getMessage(), ex);
            }
        }
    };

    private TravellerStatus getOwnStatus() {
        return ownStatus;
    }

    private void startCountdown() {
        new CountDownTimer(Constants.SCAN_DURATION_IN_MILLISECONDS, Constants.COUNTDOWN_INTERVAL_IN_MILLISECONDS) {

            public void onTick(long millisUntilFinished) {
                String formattedTimerText = String.format(Constants.COUNTDOWN_FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)%60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);

                layoutDataBinding.tvInstructions.setText(formattedTimerText);
            }

            public void onFinish() {

                peerToPeerFragmentViewModel.stopSavingNewPeersInCache();
                instructUser();
            }
        }.start();
    }

    private void instructUser() {
        if(peerToPeerFragmentViewModel.getFilteredPeerTravellers().size() > 0 && peerToPeerFragmentViewModel.getAmIGroupOwner()){
            layoutDataBinding.tvInstructions.setTextSize(20);
            layoutDataBinding.tvInstructions.setText(Constants.WAIT_FOR_OTHERS);
            layoutDataBinding.tvSearching.setText(Constants.LISTENING_TO_FELLOW_TRAVELLERS);
        }
        else if(peerToPeerFragmentViewModel.getFilteredPeerTravellers().size() > 0){
            layoutDataBinding.tvInstructions.setText(Constants.MEET_AT);
            layoutDataBinding.tvSearching.setText(Constants.LISTENING_TO_FELLOW_TRAVELLERS);
        }
        else {
            layoutDataBinding.tvInstructions.setText(Constants.ENJOY_YOUR_OWN_COMPANY);
            layoutDataBinding.cvSoloTravel.setVisibility(View.VISIBLE);
            layoutDataBinding.startNavigation.show();
        }

        layoutDataBinding.startNavigation.show();
    }

    private void askLocationPermissionIfRequired() {
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            isLocationPermissionGranted = false;

            ActivityCompat.requestPermissions(this.getActivity()
                    , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}
                    , Constants.LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            isLocationPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_REQUEST_CODE: {
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

        peerToPeerFragmentViewModel.resetFilteredPeerTravellers();
        refreshRecyclerView();
        super.onPause();
    }

    @Override
    public void onDestroy() {

        peerToPeerFragmentViewModel.resetFilteredPeerTravellers();
        refreshRecyclerView();
        super.onDestroy();
    }

    //endregion

    //region Click Handlers

    private void handleStartNavigationClick() {

        if(peerToPeerFragmentViewModel.getAmIGroupOwner()) {
            peerCommunicator.broadcastTravellers(TravellerStatus.JourneyStarted);
        }
        else {
            peerCommunicator.broadcastTravellers(TravellerStatus.TravellingToStartPoint);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("destLocations", peerToPeerFragmentViewModel.getTravelPath());

        Gson gson = new Gson();
        bundle.putString("UserList", gson.toJson(peerToPeerFragmentViewModel.getFilteredPeerTravellers()));
        bundle.putBoolean("multiDestination", peerToPeerFragmentViewModel.getFilteredPeerTravellers().size() > 0);
        bundle.putBoolean("IsGroupOwner", peerToPeerFragmentViewModel.getAmIGroupOwner());
        bundle.putInt("GroupOwnerId", peerToPeerFragmentViewModel.getGroupOwnerId());

        OfflineMaps offlineMapsFragment = new OfflineMaps();
        offlineMapsFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.fragment_container, offlineMapsFragment).addToBackStack("peerFrag").commit();
    }

    //endregion

    @Override
    protected void processFellowTravellersInfo() {

        peerToPeerFragmentViewModel.setFilteredPeerTravellers();
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        mAdapter = new PeerListAdapter(this.getActivity().getApplicationContext(), peerToPeerFragmentViewModel.getFilteredPeerTravellers());
        layoutDataBinding.peerRecyclerView.setAdapter(mAdapter);
    }
}