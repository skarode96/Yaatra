package com.tcd.yaatra.repository.models;

import org.jetbrains.annotations.TestOnly;
import java.util.HashMap;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FellowTravellersCache {

    private boolean isNewInsertStopped = false;

    @Inject
    TravellerInfo ownTravellerInfo;

    @Inject
    public FellowTravellersCache(){}

    private final HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();

    public HashMap<Integer, TravellerInfo> getFellowTravellers(){
        HashMap<Integer, TravellerInfo> clonedFellowTravellers = new HashMap<>(fellowTravellers);

        clonedFellowTravellers.forEach((key, info)->{
            info.setInfoProvider(ownTravellerInfo.getUserName());
        });

        return clonedFellowTravellers;
    }

    public boolean addOrUpdate(HashMap<Integer, TravellerInfo> receivedPeerTravellersInfo ){
        boolean isCacheUpdated = false;

        Iterator<Integer> keyIterator = receivedPeerTravellersInfo.keySet().iterator();

        while (keyIterator.hasNext()){
            Integer userIdKey = keyIterator.next();
            TravellerInfo info = receivedPeerTravellersInfo.get(userIdKey);

            isCacheUpdated = addOrUpdateCache(userIdKey, info);
        }

        return isCacheUpdated;
    }

    public void stopNewInsert(){
        isNewInsertStopped = true;
    }

    public void reset(){
        fellowTravellers.clear();
        isNewInsertStopped = false;
    }

    private boolean addOrUpdateCache(Integer userIdKey, TravellerInfo receivedInfo) {

        boolean isCacheUpdated = false;

        //Is fellow traveller already existing in cache?
        if(fellowTravellers.containsKey(userIdKey)){

            TravellerInfo existingCachedInfo = fellowTravellers.get(userIdKey);

            isCacheUpdated = copyChangesIfAny(receivedInfo, existingCachedInfo);
        }
        //Fellow traveller not present in cache - save the information
        //Only if new information is to be recorded.
        else if(!isNewInsertStopped) {
            fellowTravellers.put(userIdKey, receivedInfo);
            isCacheUpdated = true;
        }
        return isCacheUpdated;
    }

    private boolean copyChangesIfAny(TravellerInfo receivedInfo, TravellerInfo existingCachedInfo){
        if(receivedInfo.getStatusUpdateTime().isAfter(existingCachedInfo.getStatusUpdateTime())){

            existingCachedInfo.setStatus(receivedInfo.getStatus());
            existingCachedInfo.setStatusUpdateTime(receivedInfo.getStatusUpdateTime());
            existingCachedInfo.setInfoProvider(receivedInfo.getInfoProvider());

            fellowTravellers.replace(receivedInfo.getUserId(), existingCachedInfo);

            return true;
        }

        return false;
    }

    @TestOnly
    void setOwnTravellerInfo(TravellerInfo info){
        ownTravellerInfo = info;
    }
}
