package com.tcd.yaatra.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tcd.yaatra.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FindActivity extends AppCompatActivity implements WifiP2pManager.GroupInfoListener{

    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    //Button buttonTurnOnWifi;
    Button buttonDiscoverPeers;
    WifiManager mWifiMgr;
    int mPort = 30009;
    ServerSocket mServerSocket = null;
    private boolean isWifiEnabled = false;
    private boolean isPeerDiscoveryStarted = false;

    private WifiDirectBroadcastReceiver receiver;

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private List<WifiP2pDevice> connectedPeers = new ArrayList<WifiP2pDevice>();

    public void setIsWifiP2pEnabled(boolean value){
        isWifiEnabled = value;
    }

    //Declare the timer
    Timer t = null;

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

//            if(!peerList.getDeviceList().equals(peers)){
//                peers.clear();
//
//                peers.addAll(peerList.getDeviceList());
//            }
//
//            if(peers.size()==0){
//                Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_SHORT).show();
//            }
        }
    };

//    manager.requestGroupInfo(channel, new GroupInfoListener() {
//        @Override
//        public void onGroupInfoAvailable(WifiP2pGroup group) {
//            String groupPassword = group.getPassphrase();
//        }
//    });



    public void setPeers(List<WifiP2pDevice> p){

        for(WifiP2pDevice device: p){
            if(connectedPeers.indexOf(device) == -1 && peers.indexOf(device) == -1) {
                peers.add(device);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        //buttonTurnOnWifi = findViewById(R.id.buttonTurnOnWiFi);
        buttonDiscoverPeers = findViewById(R.id.buttonDiscoverPeers);



        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        /*manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        mWifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mWifiMgr.setWifiEnabled(false);*/

        /*button = findViewById(R.id.button);

        mWifiP2pMgr = (WifiP2pManager) getApplicationContext().getSystemService(WIFI_P2P_SERVICE);
        mChannel = mWifiP2pMgr.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mWifiP2pMgr, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);*/

//        buttonTurnOnWifi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isWifiEnabled) {
//                    mWifiMgr.setWifiEnabled(false);
//                    isWifiEnabled = false;
//                    buttonTurnOnWifi.setText("Turn on wifi");
//                } else {
//                    mWifiMgr.setWifiEnabled(true);
//                    isWifiEnabled = true;
//                    buttonTurnOnWifi.setText("Turn off wifi");
//                }
//            }
//        });

        /*buttonDiscoverPeers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isWifiEnabled) {
                    mWifiMgr.setWifiEnabled(false);
                    isWifiEnabled = false;
                    //buttonTurnOnWifi.setText("Turn on wifi");
                } else {
                    mWifiMgr.setWifiEnabled(true);
                    isWifiEnabled = true;
                    //buttonTurnOnWifi.setText("Turn off wifi");
                }

                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        // Code for when the discovery initiation is successful goes here.
                        // No services have actually been discovered yet, so this method
                        // can often be left blank. Code for peer discovery goes in the
                        // onReceive method, detailed below.
                        //activity.displayDialog("Success");

                        // Request available peers from the wifi p2p manager. This is an
                        // asynchronous call and the calling activity is notified with a
                        // callback on PeerListListener.onPeersAvailable()
                        *//*if (wifiP2pManager != null) {
                            wifiP2pManager.requestPeers(channel, peerListListener);
                        }*//*

                        Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                        buttonDiscoverPeers.setText("Started");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        // Code for when the discovery initiation fails goes here.
                        // Alert the user that something went wrong.
                        Toast.makeText(getApplicationContext(), "Failed to start discovery", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        receiver = new WifiDirectBroadcastReceiver(manager, channel, this);*/

        addClickListenerForDiscoverPeers();
    }


    private boolean isOwner = false;
    WifiP2pManager.ConnectionInfoListener connectListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {

            // InetAddress from WifiP2pInfo struct.
            if(info.groupOwnerAddress != null) {
                String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

                // After the group negotiation, we can determine the group owner
                // (server).
                if (info.groupFormed && info.isGroupOwner) {
                    // Do whatever tasks are specific to the group owner.
                    // One common case is creating a group owner thread and accepting
                    // incoming connections.
                    isOwner = true;

                    try {
                        mServerSocket = new ServerSocket(mPort);
                        Socket mSocket = mServerSocket.accept();
                    } catch (Exception e) {

                    }

                    Log.e(getClass().getSimpleName(), "Running on port: " + mServerSocket.getLocalPort());
                } else if (info.groupFormed) {
                    // The other device acts as the peer (client). In this case,
                    // you'll want to create a peer thread that connects
                    // to the group owner.
                    isOwner = false;
                }
            }

        }
    };

    @Override
    public void onGroupInfoAvailable(final WifiP2pGroup group) {

        if(group != null) {

            //System.out.println(group.toString());
            List<WifiP2pDevice> list = new ArrayList<WifiP2pDevice>();

            if(isOwner)
                list.addAll(group.getClientList());
            else
                list.add(group.getOwner());

            if(list!=null && list.size()>0){

                WifiP2pDevice device = list.get(0);

                if(peers.indexOf(device) != -1){
                    peers.remove(peers.indexOf(device));
                    showPeers();
                }

                if(connectedPeers.indexOf(device) == -1) {
                    connectedPeers.add(device);
                    showConnectedPeers();

                    Toast.makeText(FindActivity.this, device.deviceName + "Connected",
                            Toast.LENGTH_SHORT).show();
                }

                stopPeerDiscovery(false);
            }
            // InetAddress from WifiP2pInfo struct.
        }

    }



    //    @Override
    public void connect(final long deviceId) {
        // Picking the first device found on the network.
        final WifiP2pDevice device = peers.get((int)deviceId);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
                Toast.makeText(FindActivity.this, "Connect Done.",
                        Toast.LENGTH_SHORT).show();

                peers.remove((int)deviceId);
                showPeers();

                if(connectedPeers.indexOf(device) == -1) {
                    connectedPeers.add(device);
                    showConnectedPeers();
                }

                //stopPeerDiscovery(false);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(FindActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        //registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPeerDiscovery(true);
        //unregisterReceiver(receiver);
    }

    public void displayDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.create();
        builder.show();
    }

    public void showPeers(){
//        this.peers = p;

        ArrayList<String> arrayList = new ArrayList<>();

        final ListView list = findViewById(R.id.listNearbyDevices);

        for(int i=0; i<peers.size(); i++){
            arrayList.add(peers.get(i).deviceName);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                connect(id);

            }
        });
    }

    public void showConnectedPeers(){
        final ListView list = findViewById(R.id.listConnectedDevices);
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i=0; i<connectedPeers.size(); i++){
            arrayList.add(connectedPeers.get(i).deviceName);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void addClickListenerForDiscoverPeers(){
        final FindActivity activity = this;

        buttonDiscoverPeers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mWifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                if(!mWifiMgr.isWifiEnabled()){

                    //mWifiMgr.setWifiEnabled(true);

                    startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 20);
                    return;

                }

                if(buttonDiscoverPeers.getText().toString().equalsIgnoreCase("discover")){

                    t = new Timer();

                    //Set the schedule function and rate
                    t.schedule(new TimerTask() {

                                   @Override
                                   public void run() {

                                       if(isPeerDiscoveryStarted){
                                           unregisterReceiver(receiver);
                                           receiver = null;
                                           channel.close();
                                           manager = null;
                                           isPeerDiscoveryStarted = false;
                                       }

                                       startPeerDiscovery(activity);
                                   }

                               },
                            //Set how long before to start calling the TimerTask (in milliseconds)
                            0,
                            //Set the amount of time between each execution (in milliseconds)
                            10000);
                }
                else{
                    stopPeerDiscovery(true);
                }
            }
        });


    }

    private void startPeerDiscovery(final FindActivity activity){
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, getMainLooper(), null);

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank. Code for peer discovery goes in the
                // onReceive method, detailed below.
                //activity.displayDialog("Success");

                // Request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                                                            /*if (wifiP2pManager != null) {
                                                                wifiP2pManager.requestPeers(channel, peerListListener);
                                                            }*/

                Toast.makeText(getApplicationContext(), "Searching..", Toast.LENGTH_SHORT).show();
                buttonDiscoverPeers.setText("Stop Discovery");
                isPeerDiscoveryStarted = true;

                receiver = new WifiDirectBroadcastReceiver(manager, channel, activity);

                registerReceiver(receiver, intentFilter);
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
                Toast.makeText(getApplicationContext(), "Failed to start discovery", Toast.LENGTH_SHORT).show();
                buttonDiscoverPeers.setText("Discover");
                isPeerDiscoveryStarted = false;

                channel.close();
                channel = null;
                manager = null;
            }
        });
    }

    private void stopPeerDiscovery(final boolean closeChannel){
        if(manager!=null && isPeerDiscoveryStarted){
            manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    if(closeChannel){
                        unregisterReceiver(receiver);
                    }
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(), "Failed to stop discovery", Toast.LENGTH_SHORT).show();
                }
            });

            if (t != null) {

                t.cancel();
                t = null;
            }

            if(closeChannel) {

                channel.close();
                channel = null;
                manager = null;
                peers.clear();
                connectedPeers.clear();

                showPeers();
                showConnectedPeers();
            }

            buttonDiscoverPeers.setText("Discover");
            isPeerDiscoveryStarted = false;
        }
    }
}
