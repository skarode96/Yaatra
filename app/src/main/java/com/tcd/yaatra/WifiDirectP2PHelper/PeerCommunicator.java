package com.tcd.yaatra.WifiDirectP2PHelper;

import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;
import android.widget.Toast;

import com.tcd.yaatra.WifiDirectP2PHelper.models.Gender;
import com.tcd.yaatra.WifiDirectP2PHelper.models.TravellerInfo;
import com.tcd.yaatra.WifiDirectP2PHelper.models.TravellerStatus;
import com.tcd.yaatra.ui.activities.PeerToPeerActivity;
import com.tcd.yaatra.utils.NetworkUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import static android.content.Context.WIFI_P2P_SERVICE;

public class PeerCommunicator implements WifiP2pManager.ConnectionInfoListener {

    //region Private Variables

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifiP2pChannel;
    ServiceDiscoveryReceiver serviceDiscoveryReceiver;

    private static final String TAG = "PeerCommunicator";
    private static final String SERVICE_INSTANCE = "com.tcd.yaatra.WifiDirectService";
    private static final String SERVICE_TYPE = "tcp";
    private static final int TIMER_PERIOD_IN_MILLISECONDS = 5000;
    private static final int TIMER_DELAY_IN_MILLISECONDS = 0;

    private PeerToPeerActivity peerToPeerActivity;

    private WifiP2pDnsSdServiceRequest serviceRequest;

    private boolean isReceiverRegistered = false;

    private Timer discoveryTimer;
    private boolean isDiscoveryStarted = false;

    private HashMap<String, TravellerInfo> allTravellers;
    private String appUserName;

    //endregion

    public PeerCommunicator(PeerToPeerActivity activity, String appUserName){

        peerToPeerActivity = activity;
        this.appUserName = appUserName;

        initializeWiFiDirectComponents();

        NetworkUtils.deletePersistentGroups(wifiP2pManager, wifiP2pChannel);

        initializeUserAsTraveller();
    }

    private void initializeWiFiDirectComponents(){
        wifiP2pManager = (WifiP2pManager) peerToPeerActivity.getSystemService(WIFI_P2P_SERVICE);
        wifiP2pChannel = wifiP2pManager.initialize(peerToPeerActivity, peerToPeerActivity.getMainLooper(), null);
        serviceDiscoveryReceiver = new ServiceDiscoveryReceiver(wifiP2pManager, wifiP2pChannel, this);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
    }

    private void initializeUserAsTraveller(){

        LocalDateTime now = LocalDateTime.now();
        TravellerInfo info =
                new TravellerInfo(appUserName, 20, Gender.NotSpecified
                        , 0.0d, 0.0d, 0.0d, 0.0d
                        , TravellerStatus.None, now, 0.0d
                        , NetworkUtils.getWiFiIPAddress(peerToPeerActivity)
                        , 12345, now);

        allTravellers = new HashMap<>();
        allTravellers.put(info.getUserName(), info);
    }

    public void advertiseStatusAndDiscoverFellowTravellers(TravellerStatus status){

        setCurrentStatusOfAppUser(status);

        stopServiceDiscoveryTimer();

        wifiP2pManager.clearLocalServices(wifiP2pChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

                Log.d(TAG, "Removed all existing wifi direct local services");

                Map<String, String> serializedRecord = P2pSerializerDeserializer.serializeToMap(allTravellers.values());

                WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                        SERVICE_INSTANCE, SERVICE_TYPE, serializedRecord);

                wifiP2pManager.addLocalService(wifiP2pChannel, service, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Started advertising status of travellers");
                        subscribeStatusChangeOfPeers();
                    }

                    @Override
                    public void onFailure(int error) {
                        Log.e(TAG, "Error: Failed to advertise status");
                    }
                });
            }

            @Override
            public void onFailure(int error) {
                Log.e(TAG, "Error: Failed to remove existing wifi direct services");
            }
        });
    }

    private void setCurrentStatusOfAppUser(TravellerStatus status){
        TravellerInfo info = allTravellers.get(appUserName);
        info.setStatus(status);
        allTravellers.replace(appUserName, info);
    }

    private void subscribeStatusChangeOfPeers(){

        registerListenersForFellowTravellers();

        wifiP2pManager.removeServiceRequest(wifiP2pChannel, serviceRequest, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Removed existing service discovery request from framework");

                wifiP2pManager.addServiceRequest(wifiP2pChannel, serviceRequest,
                        new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Added service discovery request to framework");
                            }

                            @Override
                            public void onFailure(int arg0) {
                                Log.d(TAG, "Error: Failed to add service discovery request to framework");
                            }
                        });

                startDiscoveringFellowTravellersOnTimer();
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "Error: Failed to remove service discovery request from framework");
            }
        });
    }

    private void registerListenersForFellowTravellers() {
        wifiP2pManager.setDnsSdResponseListeners(wifiP2pChannel,
            new WifiP2pManager.DnsSdServiceResponseListener() {

                @Override
                public void onDnsSdServiceAvailable(String instanceName,
                                                    String registrationType, WifiP2pDevice srcDevice) {
                    Log.d(TAG, instanceName + "####" + registrationType);
                    // A service has been discovered. Is this our app?
                    if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                        // yes it is
                    } else {
                        //no it isn't
                    }
                }
            },
            new WifiP2pManager.DnsSdTxtRecordListener() {

                @Override
                public void onDnsSdTxtRecordAvailable(
                        String fullDomainName, Map<String, String> travellersInfoMap,
                        WifiP2pDevice device) {

                    //Check if published data is from our app service
                    if (fullDomainName.toLowerCase().startsWith(SERVICE_INSTANCE.toLowerCase())) {

                        Log.d(TAG, "Received advertised information from peers");

                        Toast.makeText(peerToPeerActivity, "Peer Found", Toast.LENGTH_SHORT);

                        //Save or Update existing information about peer traveller
                        HashMap<String, TravellerInfo> fellowTravellers = P2pSerializerDeserializer.deserializeFromMap(travellersInfoMap);
                        fellowTravellers.forEach((fellowTravellerUserName, fellowTravellerInfo) -> cacheFellowTravellersInfo(fellowTravellerUserName, fellowTravellerInfo));

                        //Send information of fellow travellers to UI
                        HashMap<String, TravellerInfo> onlyPeerTravellers = new HashMap<>(allTravellers);
                        TravellerInfo userInfo = onlyPeerTravellers.remove(appUserName);
                        peerToPeerActivity.showFellowTravellers(onlyPeerTravellers);

                        //Start advertising newly discovered fellow travellers
                        advertiseStatusAndDiscoverFellowTravellers(userInfo.getStatus());
                    }
                }
            }
        );
    }

    private void cacheFellowTravellersInfo(String fellowTravellerUserName, TravellerInfo fellowTravellerInfo){

        if(fellowTravellerUserName != appUserName && allTravellers.containsKey(fellowTravellerUserName)){
            allTravellers.replace(fellowTravellerUserName, fellowTravellerInfo);
        }
        else if(fellowTravellerUserName != appUserName){
            allTravellers.put(fellowTravellerUserName, fellowTravellerInfo);
        }
    }

    private void startDiscoveringFellowTravellersOnTimer(){
        discoveryTimer = new Timer();

        //Set the schedule function and rate
        discoveryTimer.schedule(new TimerTask() {

           @Override
           public void run() {

               wifiP2pManager.discoverServices(wifiP2pChannel, new WifiP2pManager.ActionListener() {

                   @Override
                   public void onSuccess() {
                       Log.d(TAG, "Service discovery initiated");
                   }

                   @Override
                   public void onFailure(int arg0) {
                       Log.d(TAG, "Service discovery failed: " + arg0);
                   }
               });
               }
            },
            //Set how long before to start calling the TimerTask (in milliseconds)
            TIMER_DELAY_IN_MILLISECONDS,
            //Set the amount of time between each execution (in milliseconds)
            TIMER_PERIOD_IN_MILLISECONDS);

        isDiscoveryStarted = true;
    }

    private void stopServiceDiscoveryTimer(){
        if(isDiscoveryStarted){
            discoveryTimer.cancel();
            discoveryTimer.purge();
            discoveryTimer = null;
            isDiscoveryStarted = false;
        }
    }

    public void registerPeerActivityListener(){
        IntentFilter wifiP2pFilter = new IntentFilter();
        wifiP2pFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiP2pFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //wifiP2pFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        serviceDiscoveryReceiver = new ServiceDiscoveryReceiver(wifiP2pManager,
                wifiP2pChannel, this);
        peerToPeerActivity.registerReceiver(serviceDiscoveryReceiver, wifiP2pFilter);

        isReceiverRegistered = true;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
    }

    public void cleanup(){

        stopServiceDiscoveryTimer();

        if(isReceiverRegistered) {
            peerToPeerActivity.unregisterReceiver(serviceDiscoveryReceiver);
            isReceiverRegistered = false;
        }

        wifiP2pManager.clearServiceRequests(wifiP2pChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Removed service discovery request from framework");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "Error: Failed to remove service discovery request from framework");
            }
        });

        wifiP2pManager.clearLocalServices(wifiP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Removed all wifi direct local services");
            }

            @Override
            public void onFailure(int error) {
                Log.e(TAG, "Error: Failed to remove wifi direct services");
            }
        });

        serviceRequest = null;
        serviceDiscoveryReceiver = null;
        wifiP2pChannel.close();
        wifiP2pChannel = null;
        wifiP2pManager = null;

    }
}
