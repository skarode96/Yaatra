package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.ViewModel;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tcd.yaatra.repository.models.FellowTravellersCache;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;
import com.tcd.yaatra.utils.MapUtils;

import org.jetbrains.annotations.TestOnly;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import javax.inject.Inject;

public class PeerToPeerFragmentViewModel extends ViewModel {

    @Inject
    TravellerInfo ownTravellerInfo;

    @Inject
    FellowTravellersCache fellowTravellersCache;

    boolean isInitialized;

    private ArrayList<TravellerInfo> filteredPeerTravellers = new ArrayList<>();
    private ArrayList<LatLng> travelPath;
    private boolean isGroupOwner = false;

    @Inject
    public PeerToPeerFragmentViewModel(){}

    public boolean getIsInitialized(){
        return isInitialized;
    }

    public void setIsInitialized(boolean isInitialized){
        this.isInitialized = isInitialized;
    }

    public void initializeOwnTraveller(UserInfo userInfo, String ipAddress){

        LocalDateTime now = LocalDateTime.now();

        ownTravellerInfo.setUserId(userInfo.getId());
        ownTravellerInfo.setUserName(userInfo.getUsername());
        ownTravellerInfo.setAge(userInfo.getAge());
        ownTravellerInfo.setGender(Gender.valueOfIdName(userInfo.getGender()));
        ownTravellerInfo.setUserRating(userInfo.getRating());
        ownTravellerInfo.setIpAddress(ipAddress);
        ownTravellerInfo.setRequestStartTime(now);
        ownTravellerInfo.setStatusUpdateTime(now);
        ownTravellerInfo.setInfoProvider(userInfo.getUsername());

        setupTravelPathAndGroupOwnership();

        isInitialized = true;
    }

    public void setFilteredPeerTravellers(){
        filteredPeerTravellers.clear();
        ArrayList<TravellerInfo> peerTravellerArrayList = new ArrayList<>(fellowTravellersCache.getFellowTravellers().values());
        MapUtils.filterFellowTravellers(ownTravellerInfo, peerTravellerArrayList).forEach(travellerInfo -> filteredPeerTravellers.add(travellerInfo));

        setupTravelPathAndGroupOwnership();
    }

    public ArrayList<TravellerInfo> getFilteredPeerTravellers(){
        return filteredPeerTravellers;
    }

    public void resetFilteredPeerTravellers(){
        filteredPeerTravellers.clear();
    }

    public ArrayList<LatLng> getTravelPath(){
        return travelPath;
    }

    public boolean getAmIGroupOwner(){
        return isGroupOwner;
    }

    public void stopSavingNewPeersInCache(){
        fellowTravellersCache.stopNewInsert();
    }

    //check if current user is group owner of the travellers list
    //Initialize travel path including destinations of all travellers
    //Travel path also includes group owner's start location as a destination for other travellers
    private boolean setupTravelPathAndGroupOwnership() {

        travelPath = new ArrayList<>();
        LocalDateTime leastFellowTravellerRequestStartTime = LocalDateTime.MAX;

        double groupOwnerSourceLat = 0.0;
        double groupOwnerSourceLong = 0.0;

        Iterator<TravellerInfo> iterator = filteredPeerTravellers.iterator();

        while(iterator.hasNext()){

            TravellerInfo info = iterator.next();

            if(info.getRequestStartTime().isBefore(leastFellowTravellerRequestStartTime)){
                leastFellowTravellerRequestStartTime = info.getRequestStartTime();
                groupOwnerSourceLat = info.getSourceLatitude();
                groupOwnerSourceLong = info.getSourceLongitude();
            }

            travelPath.add(new LatLng(info.getDestinationLatitude(), info.getDestinationLongitude()));
        }

        isGroupOwner = ownTravellerInfo.getRequestStartTime().isBefore(leastFellowTravellerRequestStartTime);

        if(!isGroupOwner){
            travelPath.add(0, new LatLng(groupOwnerSourceLat, groupOwnerSourceLong));
        }

        travelPath.add(0, new LatLng(ownTravellerInfo.getSourceLatitude(), ownTravellerInfo.getSourceLongitude()));
        travelPath.add(new LatLng(ownTravellerInfo.getDestinationLatitude(), ownTravellerInfo.getDestinationLongitude()));

        return isGroupOwner;
    }

    @TestOnly
    public void setMockObjects(TravellerInfo mockTraveller, FellowTravellersCache mockCache){
        this.ownTravellerInfo = mockTraveller;
        this.fellowTravellersCache = mockCache;
    }
}
