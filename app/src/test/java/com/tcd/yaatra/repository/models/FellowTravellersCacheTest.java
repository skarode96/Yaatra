package com.tcd.yaatra.repository.models;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.Assert.*;

public class FellowTravellersCacheTest {

    FellowTravellersCache testObjectFellowTravellersCache;
    TravellerInfo ownTraveller;

    @Before
    public void setup(){
        testObjectFellowTravellersCache = new FellowTravellersCache();

        ownTraveller = getDummyTraveller(1,"TestUser");
        testObjectFellowTravellersCache.setOwnTravellerInfo(ownTraveller);
    }

    @Test
    public void addOrUpdate_NewFellowTraveller_AddsToCacheWithInfoProviderAsSelf() {

        testObjectFellowTravellersCache.reset();

        assertTrue(testObjectFellowTravellersCache.getFellowTravellers().isEmpty());

        Integer fellowTravellerUserId = 2;
        String fellowTravellerUserName = "FellowTraveller";

        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();
        fellowTravellers.put(fellowTravellerUserId, getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName));
        testObjectFellowTravellersCache.addOrUpdate(fellowTravellers);

        HashMap<Integer, TravellerInfo> cacheEntry = testObjectFellowTravellersCache.getFellowTravellers();
        assertEquals(1, cacheEntry.size());
        assertTrue(cacheEntry.containsKey(fellowTravellerUserId));

        TravellerInfo cachedTraveller = cacheEntry.get(fellowTravellerUserId);

        assertEquals(ownTraveller.getUserName(), cachedTraveller.getInfoProvider());
    }

    @Test
    public void addOrUpdate_InfoExistsInCache_UpdateReceived_UpdateCache() {

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
        testObjectFellowTravellersCache.reset();
        assertTrue(testObjectFellowTravellersCache.getFellowTravellers().isEmpty());

        //Send new information to cache
        fellowTravellers.put(fellowTravellerUserId, dummyTravellerFirstInfo);
        testObjectFellowTravellersCache.addOrUpdate(fellowTravellers);

        //Get info from cache and verify properties
        VerifyCachedTravellerProperties(ownTraveller.getUserName(), fellowTravellerUserId, fellowTravellerUserName, statusUpdateTime1);

        //Send updated information to cache
        fellowTravellers.replace(fellowTravellerUserId, dummyTravellerSecondInfo);
        boolean isCacheUpdated = testObjectFellowTravellersCache.addOrUpdate(fellowTravellers);

        VerifyCachedTravellerProperties(ownTraveller.getUserName(), fellowTravellerUserId, fellowTravellerUserName, statusUpdateTime2);

        assertTrue(isCacheUpdated);
    }

    @Test
    public void addOrUpdate_InfoExistsInCache_SameInfoReceived_DoNotUpdateCache() {

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
        testObjectFellowTravellersCache.reset();
        assertTrue(testObjectFellowTravellersCache.getFellowTravellers().isEmpty());

        //Send new information to cache
        fellowTravellers.put(fellowTravellerUserId, dummyTravellerFirstInfo);
        testObjectFellowTravellersCache.addOrUpdate(fellowTravellers);

        //Get info from cache and verify properties
        VerifyCachedTravellerProperties(ownTraveller.getUserName(), fellowTravellerUserId, fellowTravellerUserName, statusUpdateTime1);

        //Send same information to cache again
        fellowTravellers.replace(fellowTravellerUserId, dummyTravellerSecondInfo);
        boolean isCacheUpdated = testObjectFellowTravellersCache.addOrUpdate(fellowTravellers);

        VerifyCachedTravellerProperties(ownTraveller.getUserName(), fellowTravellerUserId, fellowTravellerUserName, statusUpdateTime2);

        assertFalse(isCacheUpdated);
    }

    @Test
    public void reset_RemovesAllEntriesFromCache() {

        testObjectFellowTravellersCache.reset();

        Integer fellowTravellerUserId = 1;
        String fellowTravellerUserName = "FellowTraveller";

        HashMap<Integer, TravellerInfo> fellowTravellers = new HashMap<>();
        fellowTravellers.put(fellowTravellerUserId, getDummyTraveller(fellowTravellerUserId, fellowTravellerUserName));
        testObjectFellowTravellersCache.addOrUpdate(fellowTravellers);

        HashMap<Integer, TravellerInfo> cacheEntry = testObjectFellowTravellersCache.getFellowTravellers();
        assertEquals(1, cacheEntry.size());

        testObjectFellowTravellersCache.reset();

        cacheEntry = testObjectFellowTravellersCache.getFellowTravellers();
        assertEquals(0, cacheEntry.size());
    }

    @NotNull
    private TravellerInfo VerifyCachedTravellerProperties(String appUserName
            , Integer fellowTravellerUserId, String fellowTravellerUserName
            , LocalDateTime statusUpdateTime) {
        HashMap<Integer, TravellerInfo> cacheEntry = testObjectFellowTravellersCache.getFellowTravellers();
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
