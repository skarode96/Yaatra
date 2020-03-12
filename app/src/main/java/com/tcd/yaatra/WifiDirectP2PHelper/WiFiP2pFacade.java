package com.tcd.yaatra.WifiDirectP2PHelper;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;
import android.util.Log;
import com.tcd.yaatra.utils.NetworkUtils;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static android.content.Context.WIFI_P2P_SERVICE;

/*********************************************************************************************************/
/*This class is responsible to connect to WiFiP2pManager service and broadcast payload to other listeners*/
/*This class is agnostic of the application functionality.*/
/*It creates separate P2p service for each incoming record and publishes the same*/
/*Also it listens to the peer services with same service instance type*/
/*Once a record is received from peer devices, it forwards the response to its parent*/
/*********************************************************************************************************/
public class WiFiP2pFacade implements WifiP2pManager.ConnectionInfoListener {

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiP2pChannel;
    private ServiceDiscoveryReceiver serviceDiscoveryReceiver;
    private Handler serviceBroadcastingHandler;

    private static final String TAG = "WiFiP2pFacade";
    private static final String SERVICE_INSTANCE = "com.tcd.yaatra.WifiDirectService";
    private static final String SERVICE_TYPE = "tcp";
    private static final int MIN_BROADCAST_INTERVAL = 4111;
    private static final int MAX_BROADCAST_INTERVAL = 8111;

    PeerListener peerListener;
    Activity parentActivity;

    private boolean isRunning;
    private boolean isReceiverRegistered;

    //region Singleton implementation

    private static final WiFiP2pFacade instance = new WiFiP2pFacade();

    private WiFiP2pFacade() {
    }

    public static WiFiP2pFacade getInstance() {
        return instance;
    }

    //endregion

    public void initialize(Activity activity, PeerListener listener) {

        peerListener = listener;
        this.parentActivity = activity;

        wifiP2pManager = (WifiP2pManager) activity.getSystemService(WIFI_P2P_SERVICE);
        wifiP2pChannel = wifiP2pManager.initialize(activity.getApplicationContext(), activity.getMainLooper(), null);
        serviceDiscoveryReceiver = new ServiceDiscoveryReceiver(wifiP2pManager, wifiP2pChannel, this);
        registerPeerActivityListener();

        serviceBroadcastingHandler = new Handler();

        NetworkUtils.deletePersistentGroups(wifiP2pManager, wifiP2pChannel);
    }

    public void broadcastInformationAndListenToPeers(List<Map<String, String>> recordsToBroadcast) {

        if(peerListener == null || parentActivity == null){
            throw new IllegalStateException("WiFiP2pFacade is not initialized.");
        }

        cleanup(false);

        wifiP2pManager.clearLocalServices(wifiP2pChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Removed all existing wifi direct local services");

                addLocalServices(recordsToBroadcast);
            }

            @Override
            public void onFailure(int error) {
                Log.e(TAG, "Error: Failed to remove existing wifi direct services, error: " + error);
            }
        });
    }

    private void addLocalServices(List<Map<String, String>> recordsToBroadcast) {

        Map<String, String> firstRecord = recordsToBroadcast.remove(0);

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_TYPE, firstRecord);

        wifiP2pManager.addLocalService(wifiP2pChannel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                Log.d(TAG, "Added local service for: " + firstRecord.toString());

                if (recordsToBroadcast.size() == 0) {

                    registerListenersForPeerServices();

                    mServiceBroadcastingRunnable.run();
                } else {
                    addLocalServices(recordsToBroadcast);
                }
            }

            @Override
            public void onFailure(int reason) {

                Log.e(TAG, "Error: Failed to add local service for: " + firstRecord.toString() + " Reason Code: " + reason);
            }
        });
    }

    private void registerListenersForPeerServices() {
        wifiP2pManager.setDnsSdResponseListeners(wifiP2pChannel,
                new WifiP2pManager.DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {
                        Log.d(TAG, instanceName + "####" + registrationType);

                        // A service has been discovered. Is this our app?
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            Log.d(TAG, "Same App Service Discovered");
                            // yes it is
                        } else {
                            //no it isn't
                        }
                    }
                },
                new WifiP2pManager.DnsSdTxtRecordListener() {

                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> receivedInfo,
                            WifiP2pDevice device) {

                        //Check if published data is from our app service
                        if (fullDomainName.toLowerCase().startsWith(SERVICE_INSTANCE.toLowerCase())) {

                            Log.d(TAG, "Received advertised information from peers");

                            peerListener.processPeerServiceInfo(receivedInfo);
                        }
                    }
                }
        );
    }

    private Runnable mServiceBroadcastingRunnable = new Runnable() {
        @Override
        public void run() {

            wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Stopped peer discovery");

                    wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                            Log.d(TAG, "Started peer discovery");

                            addServiceRequestAndDiscoverServices();
                        }

                        @Override
                        public void onFailure(int error) {

                            Log.d(TAG, "Failed to start peer discovery, error: " + error);
                        }
                    });
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "Failed to stop peer discovery, reason: " + reason);
                }
            });

            serviceBroadcastingHandler
                    .postDelayed(mServiceBroadcastingRunnable, getRandomServiceBroadcastingInterval());
        }
    };

    private void addServiceRequestAndDiscoverServices() {
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        wifiP2pManager.removeServiceRequest(wifiP2pChannel, serviceRequest, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Removed existing service discovery request from framework");

                wifiP2pManager.addServiceRequest(wifiP2pChannel, serviceRequest,
                        new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Added service discovery request to framework");

                                startDiscoveringPeerServices();
                            }

                            @Override
                            public void onFailure(int arg0) {
                                Log.d(TAG, "Error: Failed to add service discovery request to framework");
                            }
                        });
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "Error: Failed to remove service discovery request from framework");
            }
        });
    }

    private void startDiscoveringPeerServices() {

        wifiP2pManager.discoverServices(wifiP2pChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Service discovery initiated");

                isRunning = true;
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Service discovery failed: " + reason);
            }
        });
    }

    private int getRandomServiceBroadcastingInterval() {
        return (new Random()).nextInt((MAX_BROADCAST_INTERVAL - MIN_BROADCAST_INTERVAL) + 1) + MIN_BROADCAST_INTERVAL;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
    }

    public void registerPeerActivityListener() {
        IntentFilter wifiP2pFilter = new IntentFilter();
        wifiP2pFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiP2pFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        wifiP2pFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        serviceDiscoveryReceiver = new ServiceDiscoveryReceiver(wifiP2pManager,
                wifiP2pChannel, this);
        parentActivity.registerReceiver(serviceDiscoveryReceiver, wifiP2pFilter);

        isReceiverRegistered = true;
    }

    public void cleanup(boolean isDestroy) {

        if (isRunning) {
            serviceBroadcastingHandler.removeCallbacksAndMessages(null);
            serviceBroadcastingHandler = new Handler();
        }

        wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Stopped peer discovery");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Failed to stop peer discovery");
            }
        });

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

        if (isDestroy) {

            if (isReceiverRegistered) {
                parentActivity.unregisterReceiver(serviceDiscoveryReceiver);
                isReceiverRegistered = false;
            }

            serviceBroadcastingHandler = null;
            serviceDiscoveryReceiver = null;
            wifiP2pChannel.close();
            wifiP2pChannel = null;
            wifiP2pManager = null;

            isRunning = false;
        }
    }
}
