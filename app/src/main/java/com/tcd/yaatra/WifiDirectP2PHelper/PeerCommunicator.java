package com.tcd.yaatra.WifiDirectP2PHelper;

import com.tcd.yaatra.repository.models.FellowTravellersCache;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PeerCommunicator implements PeerListener {

    //region Private Variables

    private FellowTravellersSubscriberActivity parentActivity;
    private TravellerInfo ownTravellerInfo;
    private boolean isInitialized = false;

    //endregion

    @Inject
    public PeerCommunicator(){}

    public void initialize(FellowTravellersSubscriberActivity activity, TravellerInfo travellerInfo) {

        parentActivity = activity;
        ownTravellerInfo = travellerInfo;

        WiFiP2pFacade.getInstance().initialize(parentActivity, this);

        isInitialized = true;
    }

    public void advertiseStatusAndDiscoverFellowTravellers() {

        if (!isInitialized || ownTravellerInfo == null){
            throw new IllegalStateException("Peer communicator is not initialized.");
        }

        HashMap<Integer, TravellerInfo> allTravellers = FellowTravellersCache.getCacheInstance().getFellowTravellers(SharedPreferenceUtils.getUserName());
        allTravellers.put(ownTravellerInfo.getUserId(), ownTravellerInfo);

        List<Map<String, String>> recordsToBroadcast = P2pSerializerDeserializer.serializeToMap(allTravellers.values());

        WiFiP2pFacade.getInstance().broadcastInformationAndListenToPeers(recordsToBroadcast);
    }

    @Override
    public void processPeerServiceInfo(Map<String, String> receivedInfo) {

        //Save or Update existing information about peer traveller
        HashMap<Integer, TravellerInfo> fellowTravellers = P2pSerializerDeserializer.deserializeFromMap(receivedInfo);

        HashMap<Integer, TravellerInfo> onlyPeerTravellers = new HashMap<>(fellowTravellers);
        onlyPeerTravellers.remove(ownTravellerInfo.getUserId());

        boolean isCacheUpdated = FellowTravellersCache.getCacheInstance().addOrUpdate(onlyPeerTravellers);

        if (isCacheUpdated) {

            parentActivity.processFellowTravellersInfo(FellowTravellersCache.getCacheInstance().getFellowTravellers(ownTravellerInfo.getUserName()));

            //Start advertising newly discovered/updated fellow travellers
            advertiseStatusAndDiscoverFellowTravellers();
        }
    }

    public void updateOwnTraveller(TravellerInfo travellerInfo) {
        ownTravellerInfo = travellerInfo;
    }

    public void cleanup() {
        if(isInitialized) {
            WiFiP2pFacade.getInstance().cleanup(true);
            ownTravellerInfo = null;

            isInitialized = false;
        }
    }
}
