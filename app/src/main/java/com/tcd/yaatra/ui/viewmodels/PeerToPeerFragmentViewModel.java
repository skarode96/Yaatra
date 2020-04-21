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
import java.util.stream.Collectors;
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
    private int groupOwnerId = 0;
    private int preferredGender = Gender.NOT_SPECIFIED.idNumber;

    @Inject
    public PeerToPeerFragmentViewModel(){}

    public boolean getIsInitialized(){
        return isInitialized;
    }

    public int getGroupOwnerId(){ return groupOwnerId; }

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

        preferredGender = userInfo.getPref_gender();

        setupTravelPathAndGroupOwnership();

        isInitialized = true;
    }

    public void setFilteredPeerTravellers(){
        filteredPeerTravellers.clear();
        ArrayList<TravellerInfo> peerTravellerArrayList = new ArrayList<>(fellowTravellersCache.getFellowTravellers().values());
        MapUtils.filterFellowTravellers(ownTravellerInfo, peerTravellerArrayList).forEach(travellerInfo -> filteredPeerTravellers.add(travellerInfo));

        filteredPeerTravellers = filterTravellersAsPerUserPreference(filteredPeerTravellers);

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

        ArrayList<LatLng> destinations = new ArrayList<>();
        LatLng ownTravellerSourceLocation = new LatLng(ownTravellerInfo.getSourceLatitude(), ownTravellerInfo.getSourceLongitude());

        LocalDateTime leastFellowTravellerRequestStartTime = LocalDateTime.MAX;

        double groupOwnerSourceLat = 0.0;
        double groupOwnerSourceLong = 0.0;
        groupOwnerId = 0;

        Iterator<TravellerInfo> iterator = filteredPeerTravellers.iterator();

        while(iterator.hasNext()){

            TravellerInfo info = iterator.next();

            if(info.getRequestStartTime().isBefore(leastFellowTravellerRequestStartTime)){
                leastFellowTravellerRequestStartTime = info.getRequestStartTime();
                groupOwnerId = info.getUserId();
                groupOwnerSourceLat = info.getSourceLatitude();
                groupOwnerSourceLong = info.getSourceLongitude();
            }

            destinations.add(new LatLng(info.getDestinationLatitude(), info.getDestinationLongitude()));
        }

        destinations.add(new LatLng(ownTravellerInfo.getDestinationLatitude(), ownTravellerInfo.getDestinationLongitude()));

        isGroupOwner = ownTravellerInfo.getRequestStartTime().isBefore(leastFellowTravellerRequestStartTime);

        if(!isGroupOwner){

            LatLng groupOwnerSourceLocation = new LatLng(groupOwnerSourceLat, groupOwnerSourceLong);
            destinations = MapUtils.sortDestinationsByDistance(groupOwnerSourceLocation, destinations);

            travelPath.add(0, groupOwnerSourceLocation);
        }
        else {
            groupOwnerId = ownTravellerInfo.getUserId();
            destinations = MapUtils.sortDestinationsByDistance(ownTravellerSourceLocation, destinations);
        }

        travelPath.add(0, ownTravellerSourceLocation);
        travelPath.addAll(destinations);

        return isGroupOwner;
    }

    private ArrayList<TravellerInfo> filterTravellersAsPerUserPreference(ArrayList<TravellerInfo> filteredPeerTravellers) {

        ArrayList<TravellerInfo> preferredPeers = filteredPeerTravellers;

        //Consider Preferred Gender and Mode of Travel
        switch (Gender.valueOfIdNumber(preferredGender)){

            case MALE: preferredPeers = new ArrayList<>(filteredPeerTravellers
                                                        .stream()
                                                        .filter(t->t.getGender() == Gender.MALE
                                                                    && t.getModeOfTravel() == ownTravellerInfo.getModeOfTravel())
                                                        .collect(Collectors.toList()));
                        break;

            case FEMALE: preferredPeers = new ArrayList<>(filteredPeerTravellers
                    .stream()
                    .filter(t->t.getGender() == Gender.FEMALE
                            && t.getModeOfTravel() == ownTravellerInfo.getModeOfTravel())
                    .collect(Collectors.toList()));
                    break;

            case NOT_SPECIFIED:
            case OTHER:
                preferredPeers = new ArrayList<>(filteredPeerTravellers
                        .stream()
                        .filter(t -> t.getModeOfTravel() == ownTravellerInfo.getModeOfTravel())
                        .collect(Collectors.toList()));
                        break;
        }

        return preferredPeers;
    }

    @TestOnly
    public void setMockObjects(TravellerInfo mockTraveller, FellowTravellersCache mockCache){
        this.ownTravellerInfo = mockTraveller;
        this.fellowTravellersCache = mockCache;
    }
}
