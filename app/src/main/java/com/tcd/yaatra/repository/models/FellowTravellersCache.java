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

    public HashMap<Integer, TravellerInfo> getFellowTravellers(){
        return new HashMap<>(fellowTravellers);
    }

    public boolean addOrUpdate(String appUserName, HashMap<Integer, TravellerInfo> receivedPeerTravellersInfo ){
        boolean isCacheUpdated = false;

        Iterator<Integer> keyIterator = receivedPeerTravellersInfo.keySet().iterator();

        while (keyIterator.hasNext()){
            Integer userIdKey = keyIterator.next();
            TravellerInfo info = receivedPeerTravellersInfo.get(userIdKey);

            isCacheUpdated = addOrUpdateCache(userIdKey, appUserName, info);
        }

        return isCacheUpdated;
    }

    public void clear(){
        fellowTravellers.clear();
    }

    private boolean addOrUpdateCache(Integer userIdKey, String appUserName, TravellerInfo receivedInfo) {

        boolean isCacheUpdated = false;

        //Is fellow traveller already existing in cache?
        if(fellowTravellers.containsKey(userIdKey)){

            TravellerInfo existingCachedInfo = fellowTravellers.get(userIdKey);

            if(!isInformationReceivedThroughHop(receivedInfo)
                && !existingCachedInfo.getInfoProvider().equals(appUserName)){
                //This information is directly provided by the fellow traveller
                //However, existing cached information was received through a hop
                //Provide newly received information to other peers with provider as the current app user
                receivedInfo.setInfoProvider(appUserName);

                fellowTravellers.replace(receivedInfo.getUserId(), receivedInfo);
                isCacheUpdated = true;
            }
            else if(!isInformationReceivedThroughHop(receivedInfo)
                    && existingCachedInfo.getInfoProvider().equals(appUserName)){
                //This information is directly provided by the fellow traveller
                //Check for status updates & save if new status is received

                isCacheUpdated = copyChangesIfAny(receivedInfo, existingCachedInfo);
            }
            else if(isInformationReceivedThroughHop(receivedInfo)
                    && !existingCachedInfo.getInfoProvider().equals(appUserName)){
                //This information is provided through a hop
                //We already have received information through a hop

                //Update existing information only if received through same hop
                if(receivedInfo.getInfoProvider().equals(existingCachedInfo.getInfoProvider())) {
                    isCacheUpdated = copyChangesIfAny(receivedInfo, existingCachedInfo);
                }
                //else ignore
            }
            else if(isInformationReceivedThroughHop(receivedInfo)
                    && existingCachedInfo.getInfoProvider().equals(appUserName)){
                //This information is provided through a hop
                //However, we have already received information directly from the fellow traveller
                //Ignore
            }
        }
        //Fellow traveller not present in cache - save the information
        else {

            if(!isInformationReceivedThroughHop(receivedInfo)){
                //This information is directly provided by the fellow traveller
                //Provide this information to other peers with provider as the current app user
                receivedInfo.setInfoProvider(appUserName);
            }
            //else information is received through a hop

            fellowTravellers.put(userIdKey, receivedInfo);
            isCacheUpdated = true;
        }
        return isCacheUpdated;
    }

    private boolean isInformationReceivedThroughHop(TravellerInfo info){
        return !info.getInfoProvider().equals(info.getUserName());
    }

    private boolean copyChangesIfAny(TravellerInfo receivedInfo, TravellerInfo existingCachedInfo){
        if(receivedInfo.getStatusUpdateTime().isAfter(existingCachedInfo.getStatusUpdateTime())){

            existingCachedInfo.setStatus(receivedInfo.getStatus());
            existingCachedInfo.setStatusUpdateTime(receivedInfo.getStatusUpdateTime());

            fellowTravellers.replace(receivedInfo.getUserId(), existingCachedInfo);

            return true;
        }

        return false;
    }
}
