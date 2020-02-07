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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.WIFI_P2P_SERVICE;

public class PeerCommunicator implements WifiP2pManager.ConnectionInfoListener {

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifip2pChannel;
    ServiceDiscoveryReceiver serviceDiscoveryReceiver;

    private static final String TAG = "PeerCommunicator";
    private static final String SERVICE_INSTANCE = "com.tcd.yaatra.WifiDirectService";
    private static final String SERVICE_TYPE = "tcp";

    private PeerToPeerActivity peerToPeerActivity;

    private WifiP2pDnsSdServiceRequest serviceRequest;

    private boolean isReceiverRegistered = false;

    private Timer discoveryTimer;
    private boolean isDiscoveryStarted = false;

    public PeerCommunicator(PeerToPeerActivity activity){

        peerToPeerActivity = activity;

        wifiP2pManager = (WifiP2pManager) activity.getSystemService(WIFI_P2P_SERVICE);
        wifip2pChannel = wifiP2pManager.initialize(activity, activity.getMainLooper(), null);
        serviceDiscoveryReceiver = new ServiceDiscoveryReceiver(wifiP2pManager, wifip2pChannel, this);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        NetworkUtils.deletePersistentGroups(wifiP2pManager, wifip2pChannel);
    }

    public void StartAdvertisingMyStatus(TravellerStatus status){

        StopServiceDiscoveryTimer();

        wifiP2pManager.clearLocalServices(wifip2pChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

                TravellerInfo info = new TravellerInfo();

                info.setName("JP");
                info.setIpAddress(NetworkUtils.getWiFiIPAddress(peerToPeerActivity));
                info.setStatus(status);
                info.setSource("Source");
                info.setDestination("Destination");
                info.setAge(20);
                info.setPortNumber(12345);
                info.setRequestStartTime(LocalDateTime.now());
                info.setGender(Gender.Female);

                //Map<String, String> record = new HashMap<String, String>();
                //record.put("Name", "JShare");
                Map<String, String> serializedRecord = info.SerializeToMap();

                WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                        SERVICE_INSTANCE, SERVICE_TYPE, serializedRecord);

                wifiP2pManager.addLocalService(wifip2pChannel, service, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Added Local Service");
                        peerToPeerActivity.ListenToPeers();
                    }

                    @Override
                    public void onFailure(int error) {
                        Log.e(TAG, "ERRORCEPTION: Failed to add a service");
                    }
                });

                Log.d(TAG, "Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                Log.e(TAG, "ERRORCEPTION: Failed to clear services");
            }
        });
    }

    public void SubscribeStatusChangeOfPeers(){

        wifiP2pManager.setDnsSdResponseListeners(wifip2pChannel,
                new WifiP2pManager.DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {
                        Log.d(TAG, instanceName + "####" + registrationType);
                        // A service has been discovered. Is this our app?
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            // yes it is
                            /*WiFiP2pServiceHolder serviceHolder = new WiFiP2pServiceHolder();
                            serviceHolder.device = srcDevice;
                            serviceHolder.registrationType = registrationType;
                            serviceHolder.instanceName = instanceName;
                            deviceNames = deviceNames + srcDevice.deviceName;
                            tv.setText(deviceNames);*/
                            //connectP2p(serviceHolder);
                        } else {
                            //no it isn't
                        }
                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {

                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> travellerInfoMap,
                            WifiP2pDevice device) {
                        //boolean isGroupOwner = device.isGroupOwner();

                        if(fullDomainName.toLowerCase().startsWith(SERVICE_INSTANCE.toLowerCase())) {

                            Toast.makeText(peerToPeerActivity.getApplicationContext(), "Peer Found", Toast.LENGTH_SHORT).show();

                            TravellerInfo info = TravellerInfo.DeserializeFromMap(travellerInfoMap);

                            peerToPeerActivity.SetFellowTraveller(info);



                            //Log.d(TAG, "Peer IP: " + travellerInfoMap.get("Name"));

                            /*Log.v(TAG, Build.MANUFACTURER + ". peer port received: " + peerPort);
                            if (peerIP != null && peerPort > 0) {
                                String player = record.get("Name").toString();

                                *//*DataSender.sendCurrentDeviceData(LocalDashWiFiP2PSD.this,
                                        peerIP, peerPort, true);
                                isWDConnected = true;
                                isConnectionInfoSent = true;*//*
                            }*/
                        }
                    }
                });

        // After attaching listeners, create a service request and initiate
        // discovery.

        wifiP2pManager.removeServiceRequest(wifip2pChannel, serviceRequest, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Added service discovery request");

                wifiP2pManager.addServiceRequest(wifip2pChannel, serviceRequest,
                        new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Added service discovery request");
                            }

                            @Override
                            public void onFailure(int arg0) {
                                Log.d(TAG, "ERRORCEPTION: Failed adding service discovery request");
                            }
                        });

                StartServiceDiscoveryTimer();
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "ERRORCEPTION: Failed adding service discovery request");
            }
        });
    }

    private void StartServiceDiscoveryTimer(){
        discoveryTimer = new Timer();

        //Set the schedule function and rate
        discoveryTimer.schedule(new TimerTask() {

                       @Override
                       public void run() {

                           wifiP2pManager.discoverServices(wifip2pChannel, new WifiP2pManager.ActionListener() {

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
                0,
                //Set the amount of time between each execution (in milliseconds)
                10000);

        isDiscoveryStarted = true;
    }

    private void StopServiceDiscoveryTimer(){
        if(isDiscoveryStarted){
            discoveryTimer.cancel();
            discoveryTimer.purge();
            discoveryTimer = null;
            isDiscoveryStarted = false;
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

        /*Log.v(TAG, Build.MANUFACTURER + ". Conn info available" + wifiP2pInfo);
        Log.v(TAG, Build.MANUFACTURER + ". peer port: " + peerPort);

        if (wifiP2pInfo.groupFormed) {
            peerIP = wifiP2pInfo.groupOwnerAddress.getHostAddress();
        }

        if (!isConnectionInfoSent && peerPort > 0 && wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
            //DataSender.sendCurrentDeviceData(LocalDashWiFiP2PSD.this, peerIP, peerPort, true);
            isConnectionInfoSent = true;
        }*/
    }

    public void Cleanup(){

        StopServiceDiscoveryTimer();

        if(isReceiverRegistered) {
            peerToPeerActivity.unregisterReceiver(serviceDiscoveryReceiver);
            isReceiverRegistered = false;
        }

        wifiP2pManager.removeServiceRequest(wifip2pChannel, serviceRequest, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Added service discovery request");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "ERRORCEPTION: Failed adding service discovery request");
            }
        });

        wifiP2pManager.clearLocalServices(wifip2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                Log.e(TAG, "ERRORCEPTION: Failed to clear services");
            }
        });
    }

    public void RegisterPeerActivityListener(){
        IntentFilter wifip2pFilter = new IntentFilter();
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        serviceDiscoveryReceiver = new ServiceDiscoveryReceiver(wifiP2pManager,
                wifip2pChannel, this);
        peerToPeerActivity.registerReceiver(serviceDiscoveryReceiver, wifip2pFilter);

        isReceiverRegistered = true;
    }

}
