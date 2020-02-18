package com.tcd.yaatra.repository.models;

import java.util.HashMap;
import java.util.Iterator;

public class FellowTravellersCache {

    //region Singleton implementation

    private static final FellowTravellersCache _cacheInstance = new FellowTravellersCache();

    private FellowTravellersCache(){}

    public static FellowTravellersCache getCacheInstance(){
        return _cacheInstance;
    }

    //endregion

    private final HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();

    public HashMap<Integer, TravellerInfo> getFellowTravellers(String infoProviderUserName){
        HashMap<Integer, TravellerInfo> clonedFellowTravellers = new HashMap<>(fellowTravellers);

        clonedFellowTravellers.forEach((key, info)->{
            info.setInfoProvider(infoProviderUserName);
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

    public void clear(){
        fellowTravellers.clear();
    }

    private boolean addOrUpdateCache(Integer userIdKey, TravellerInfo receivedInfo) {

        boolean isCacheUpdated = false;

        //Is fellow traveller already existing in cache?
        if(fellowTravellers.containsKey(userIdKey)){

            TravellerInfo existingCachedInfo = fellowTravellers.get(userIdKey);

            isCacheUpdated = copyChangesIfAny(receivedInfo, existingCachedInfo);
        }
        //Fellow traveller not present in cache - save the information
        else {
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
}
