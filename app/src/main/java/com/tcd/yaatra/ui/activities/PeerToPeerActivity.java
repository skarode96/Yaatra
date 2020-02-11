package com.tcd.yaatra.ui.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.tcd.yaatra.R;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.WifiDirectP2PHelper.models.TravellerInfo;
import com.tcd.yaatra.WifiDirectP2PHelper.models.TravellerStatus;
import com.tcd.yaatra.databinding.ActivityPeerToPeerBinding;
import java.util.ArrayList;
import java.util.HashMap;

public class PeerToPeerActivity extends BaseActivity<ActivityPeerToPeerBinding> {

    PeerCommunicator communicator;
    TextView textView;
    private ArrayList<String> peers = new ArrayList<>();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    boolean isLocationPermissionGranted = false;

    //region Activity Initialization

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_peer_to_peer;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.buttonDiscoverPeers.setOnClickListener(view -> handleDiscoverButtonClick());
        layoutDataBinding.buttonSendMessage.setOnClickListener(view -> handleDiscoverButtonClick());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = layoutDataBinding.textView2;
        initializePeerCommunicator();
        checkIfLocationPermissionGranted();
    }

    private void initializePeerCommunicator(){
        communicator = new PeerCommunicator(this, "JP");
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
        peers.clear();
        showPeers();

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
        peers.clear();
        showPeers();

        if(communicator != null) {
            communicator.cleanup();
            communicator = null;
        }
        super.onDestroy();
    }

    //endregion

    private void handleDiscoverButtonClick(){
        if(isLocationPermissionGranted){
            communicator.advertiseStatusAndDiscoverFellowTravellers(TravellerStatus.SeekingFellowTraveller);
        }
    }

    public void showFellowTravellers(HashMap<String, TravellerInfo> peerTravellers){

        peers.clear();

        peerTravellers.values().forEach(info-> addPeerAddress(info.getUserName()));

        showPeers();
    }

    private void addPeerAddress(String ipAdd){
        peers.add(ipAdd);
    }

    private void showPeers(){
        final ListView list = layoutDataBinding.listNearbyDevices;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peers);
        list.setAdapter(arrayAdapter);
    }
}