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
        String fellowTravellerUserName = "FellowTraveller";

        HashMap<String, TravellerInfo> fellowTravellers = new HashMap<>();
        fellowTravellers.put(fellowTravellerUserName, getDummyTraveller(fellowTravellerUserName));
        FellowTravellersCache.getCacheInstance().addOrUpdate(appUserName, fellowTravellers);

        HashMap<String, TravellerInfo> cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers();
        assertEquals(1, cacheEntry.size());
        assertTrue(cacheEntry.containsKey(fellowTravellerUserName));

        TravellerInfo cachedTraveller = cacheEntry.get(fellowTravellerUserName);

        assertEquals(appUserName, cachedTraveller.getInfoProvider());
    }

    @Test
    public void clear_RemovesAllEntriesFromCache(){

        FellowTravellersCache.getCacheInstance().clear();

        String appUserName = "TestUser";

        HashMap<String, TravellerInfo> fellowTravellers = new HashMap<>();
        fellowTravellers.put(appUserName, getDummyTraveller(appUserName));
        FellowTravellersCache.getCacheInstance().addOrUpdate(appUserName, fellowTravellers);

        HashMap<String, TravellerInfo> cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers();
        assertEquals(1, cacheEntry.size());

        FellowTravellersCache.getCacheInstance().clear();

        cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers();
        assertEquals(0, cacheEntry.size());
    }

    private TravellerInfo getDummyTraveller(String userName){
        return new TravellerInfo(userName, 20, Gender.Male, 0.0d
                , 0.0d, 0.0d, 0.0d
                , TravellerStatus.SeekingFellowTraveller, LocalDateTime.now(), 0.0d
                , "1.2.3.4", 12345, LocalDateTime.now(), userName);
    }
}
