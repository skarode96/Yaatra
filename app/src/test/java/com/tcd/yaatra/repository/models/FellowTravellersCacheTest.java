package com.tcd.yaatra.repository.models;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.Assert.*;

public class FellowTravellersCacheTest {

    @Test
    public void addOrUpdate_NewFellowTraveller_AddsToCacheWithInfoProviderAsSelf(){

        assertTrue(FellowTravellersCache.getCacheInstance().getFellowTravellers().isEmpty());

        String appUserName = "TestUser";
        Integer appUserId = 1;
        Integer fellowTravellerUserId = 2;
        String fellowTravellerUserName = "FellowTraveller";

        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();
        fellowTravellers.put(fellowTravellerUserId, getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName));
        FellowTravellersCache.getCacheInstance().addOrUpdate(appUserName, fellowTravellers);

        HashMap<Integer, TravellerInfo> cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers();
        assertEquals(1, cacheEntry.size());
        assertTrue(cacheEntry.containsKey(fellowTravellerUserId));

        TravellerInfo cachedTraveller = cacheEntry.get(fellowTravellerUserId);

        assertEquals(appUserName, cachedTraveller.getInfoProvider());
    }

    @Test
    public void clear_RemovesAllEntriesFromCache(){

        FellowTravellersCache.getCacheInstance().clear();

        Integer fellowTravellerUserId = 1;
        String fellowTravellerUserName = "FellowTraveller";
        String appUserName = "appUser";

        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();
        fellowTravellers.put(fellowTravellerUserId, getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName));
        FellowTravellersCache.getCacheInstance().addOrUpdate(appUserName, fellowTravellers);

        HashMap<Integer, TravellerInfo> cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers();
        assertEquals(1, cacheEntry.size());

        FellowTravellersCache.getCacheInstance().clear();

        cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers();
        assertEquals(0, cacheEntry.size());
    }

    private TravellerInfo getDummyTraveller(Integer userId, String userName){
        return new TravellerInfo(userId, userName, 20, Gender.Male, 0.0d
                , 0.0d, 0.0d, 0.0d
                , TravellerStatus.SeekingFellowTraveller, LocalDateTime.now(), 0.0d
                , "1.2.3.4", 12345, LocalDateTime.now(), userName);
    }
}
