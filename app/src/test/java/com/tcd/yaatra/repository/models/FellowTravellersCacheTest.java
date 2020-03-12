package com.tcd.yaatra.repository.models;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.Assert.*;

public class FellowTravellersCacheTest {

    @Test
    public void addOrUpdate_NewFellowTraveller_AddsToCacheWithInfoProviderAsSelf() {

        FellowTravellersCache.getCacheInstance().clear();

        String appUserName = "TestUser";
        assertTrue(FellowTravellersCache.getCacheInstance().getFellowTravellers(appUserName).isEmpty());

        Integer fellowTravellerUserId = 2;
        String fellowTravellerUserName = "FellowTraveller";

        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();
        fellowTravellers.put(fellowTravellerUserId, getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName));
        FellowTravellersCache.getCacheInstance().addOrUpdate(fellowTravellers);

        HashMap<Integer, TravellerInfo> cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers(appUserName);
        assertEquals(1, cacheEntry.size());
        assertTrue(cacheEntry.containsKey(fellowTravellerUserId));

        TravellerInfo cachedTraveller = cacheEntry.get(fellowTravellerUserId);

        assertEquals(appUserName, cachedTraveller.getInfoProvider());
    }

    @Test
    public void addOrUpdate_InfoExistsInCache_UpdateReceived_UpdateCache() {

        String appUserName = "TestUser";
        Integer fellowTravellerUserId = 2;
        String fellowTravellerUserName = "FellowTraveller";
        LocalDateTime statusUpdateTime1 = LocalDateTime.now();
        LocalDateTime statusUpdateTime2 = LocalDateTime.now().plusMinutes(2);
        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();
        TravellerInfo dummyTravellerFirstInfo = getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName);
        dummyTravellerFirstInfo.setStatusUpdateTime(statusUpdateTime1);
        TravellerInfo dummyTravellerSecondInfo = getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName);
        dummyTravellerSecondInfo.setStatusUpdateTime(statusUpdateTime2);

        //Clear cache and verify its empty
        FellowTravellersCache.getCacheInstance().clear();
        assertTrue(FellowTravellersCache.getCacheInstance().getFellowTravellers(appUserName).isEmpty());

        //Send new information to cache
        fellowTravellers.put(fellowTravellerUserId, dummyTravellerFirstInfo);
        FellowTravellersCache.getCacheInstance().addOrUpdate(fellowTravellers);

        //Get info from cache and verify properties
        VerifyCachedTravellerProperties(appUserName, fellowTravellerUserId, fellowTravellerUserName, statusUpdateTime1);

        //Send updated information to cache
        fellowTravellers.replace(fellowTravellerUserId, dummyTravellerSecondInfo);
        boolean isCacheUpdated = FellowTravellersCache.getCacheInstance().addOrUpdate(fellowTravellers);

        VerifyCachedTravellerProperties(appUserName, fellowTravellerUserId, fellowTravellerUserName, statusUpdateTime2);

        assertTrue(isCacheUpdated);
    }

    @Test
    public void addOrUpdate_InfoExistsInCache_SameInfoReceived_DoNotUpdateCache() {

        String appUserName = "TestUser";
        Integer fellowTravellerUserId = 2;
        String fellowTravellerUserName = "FellowTraveller";
        LocalDateTime statusUpdateTime1 = LocalDateTime.now();
        LocalDateTime statusUpdateTime2 = statusUpdateTime1;
        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();
        TravellerInfo dummyTravellerFirstInfo = getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName);
        dummyTravellerFirstInfo.setStatusUpdateTime(statusUpdateTime1);
        TravellerInfo dummyTravellerSecondInfo = getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName);
        dummyTravellerSecondInfo.setStatusUpdateTime(statusUpdateTime2);

        //Clear cache and verify its empty
        FellowTravellersCache.getCacheInstance().clear();
        assertTrue(FellowTravellersCache.getCacheInstance().getFellowTravellers(appUserName).isEmpty());

        //Send new information to cache
        fellowTravellers.put(fellowTravellerUserId, dummyTravellerFirstInfo);
        FellowTravellersCache.getCacheInstance().addOrUpdate(fellowTravellers);

        //Get info from cache and verify properties
        VerifyCachedTravellerProperties(appUserName, fellowTravellerUserId, fellowTravellerUserName, statusUpdateTime1);

        //Send same information to cache again
        fellowTravellers.replace(fellowTravellerUserId, dummyTravellerSecondInfo);
        boolean isCacheUpdated = FellowTravellersCache.getCacheInstance().addOrUpdate(fellowTravellers);

        VerifyCachedTravellerProperties(appUserName, fellowTravellerUserId, fellowTravellerUserName, statusUpdateTime2);

        assertFalse(isCacheUpdated);
    }

    @Test
    public void clear_RemovesAllEntriesFromCache() {

        FellowTravellersCache.getCacheInstance().clear();

        Integer fellowTravellerUserId = 1;
        String fellowTravellerUserName = "FellowTraveller";
        String appUserName = "appUser";

        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();
        fellowTravellers.put(fellowTravellerUserId, getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName));
        FellowTravellersCache.getCacheInstance().addOrUpdate(fellowTravellers);

        HashMap<Integer, TravellerInfo> cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers(appUserName);
        assertEquals(1, cacheEntry.size());

        FellowTravellersCache.getCacheInstance().clear();

        cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers(appUserName);
        assertEquals(0, cacheEntry.size());
    }

    @NotNull
    private TravellerInfo VerifyCachedTravellerProperties(String appUserName
            , Integer fellowTravellerUserId, String fellowTravellerUserName
            , LocalDateTime statusUpdateTime) {
        HashMap<Integer, TravellerInfo> cacheEntry = FellowTravellersCache.getCacheInstance().getFellowTravellers(appUserName);
        assertEquals(1, cacheEntry.size());
        assertTrue(cacheEntry.containsKey(fellowTravellerUserId));

        TravellerInfo cachedTraveller = cacheEntry.get(fellowTravellerUserId);
        assertEquals(fellowTravellerUserId, cachedTraveller.getUserId());
        assertEquals(fellowTravellerUserName, cachedTraveller.getUserName());
        assertEquals(appUserName, cachedTraveller.getInfoProvider());
        assertEquals(statusUpdateTime, cachedTraveller.getStatusUpdateTime());
        return cachedTraveller;
    }

    private TravellerInfo getDummyTraveller(Integer userId, String userName) {
        TravellerInfo traveller = new TravellerInfo();
        traveller.setUserId(userId);
        traveller.setUserName(userName);
        traveller.setAge(20);
        traveller.setGender(Gender.MALE);
        traveller.setSourceLatitude(0.0d);
        traveller.setSourceLongitude(0.0d);
        traveller.setDestinationLatitude(0.0d);
        traveller.setDestinationLongitude(0.0d);
        traveller.setStatus(TravellerStatus.SeekingFellowTraveller);
        traveller.setSourceName("test");
        traveller.setDestinationName("test");
        traveller.setModeOfTravel("test");
        traveller.setRequestStartTime(LocalDateTime.now());
        traveller.setUserRating(0.0d);
        traveller.setIpAddress("1.2.3.4");
        traveller.setPortNumber(1234);
        traveller.setStatusUpdateTime(LocalDateTime.now());
        traveller.setInfoProvider(userName);

        return traveller;
    }
}
