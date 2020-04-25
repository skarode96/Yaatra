package com.tcd.yaatra.ui.viewmodels;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tcd.yaatra.repository.models.FellowTravellersCache;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;
import com.tcd.yaatra.utils.MapUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class PeerToPeerFragmentViewModelTest {

    PeerToPeerFragmentViewModel testObjectPeerToPeerFragmentViewModel;

    @Mock
    TravellerInfo mockTravellerInfo;

    @Mock
    FellowTravellersCache mockFellowTravellersCache;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);
        testObjectPeerToPeerFragmentViewModel = new PeerToPeerFragmentViewModel();
        testObjectPeerToPeerFragmentViewModel.setMockObjects(mockTravellerInfo, mockFellowTravellersCache);

    }

    @Test
    public void testSetIsInitialized(){
        testObjectPeerToPeerFragmentViewModel.setIsInitialized(true);

        Assert.assertTrue(testObjectPeerToPeerFragmentViewModel.getIsInitialized());

        testObjectPeerToPeerFragmentViewModel.setIsInitialized(false);

        Assert.assertFalse(testObjectPeerToPeerFragmentViewModel.getIsInitialized());
    }

    @Test
    public void verifyInitializeOwnTraveller(){

        UserInfo userInfo = getDummyUserInfo();

        String ipAdd = "127.0.0.1";

        Mockito.when(mockTravellerInfo.getRequestStartTime()).thenReturn(LocalDateTime.now());

        testObjectPeerToPeerFragmentViewModel.initializeOwnTraveller(userInfo, ipAdd);

        verify(mockTravellerInfo).setUserId(userInfo.getId());
        verify(mockTravellerInfo).setUserName(userInfo.getUsername());
        verify(mockTravellerInfo).setAge(userInfo.getAge());
        verify(mockTravellerInfo).setGender(Gender.valueOfIdName(userInfo.getGender()));
        verify(mockTravellerInfo).setUserRating(userInfo.getRating());
        verify(mockTravellerInfo).setInfoProvider(userInfo.getUsername());
        verify(mockTravellerInfo).setIpAddress(ipAdd);
    }

    @Test
    public void verifyListOfFilteredPeerTravellersAndTravelPathWhenOwnTravellerIsGroupOwner(){

        LocalDateTime requestTime = LocalDateTime.now();

        //Own Traveller Starts the request before everyone
        //Own Traveller should be selected as group owner
        setupOwnTravellerInfo(requestTime.minusSeconds(2));
        Mockito.when(mockTravellerInfo.getModeOfTravel()).thenReturn("Walk");

        HashMap<Integer, TravellerInfo> peerTravellers = getPeerTravellers(requestTime);

        Mockito.when(mockFellowTravellersCache.getFellowTravellers()).thenReturn(peerTravellers);

        testObjectPeerToPeerFragmentViewModel.setFilteredPeerTravellers();

        ArrayList<TravellerInfo> filteredTravellers = testObjectPeerToPeerFragmentViewModel.getFilteredPeerTravellers();

        //Verify only travellers within 1 km distance are selected
        Assert.assertEquals(2, filteredTravellers.size());
        Assert.assertEquals(peerTravellers.get(2).getUserId(), filteredTravellers.get(0).getUserId());
        Assert.assertEquals(peerTravellers.get(3).getUserId(), filteredTravellers.get(1).getUserId());

        Assert.assertTrue(testObjectPeerToPeerFragmentViewModel.getAmIGroupOwner());

        ArrayList<LatLng> travelPath = testObjectPeerToPeerFragmentViewModel.getTravelPath();

        Assert.assertEquals(4, travelPath.size());

        LatLng sourceLocation = new LatLng(mockTravellerInfo.getSourceLatitude(), mockTravellerInfo.getSourceLongitude());

        Assert.assertEquals(sourceLocation.getLatitude(), travelPath.get(0).getLatitude(), 0.0);
        Assert.assertEquals(sourceLocation.getLongitude(), travelPath.get(0).getLongitude(), 0.0);

        ArrayList<LatLng> destinations = new ArrayList<>();
        destinations.add(new LatLng(peerTravellers.get(2).getDestinationLatitude(), peerTravellers.get(2).getDestinationLongitude()));
        destinations.add(new LatLng(peerTravellers.get(3).getDestinationLatitude(), peerTravellers.get(3).getDestinationLongitude()));
        destinations.add(new LatLng(mockTravellerInfo.getDestinationLatitude(), mockTravellerInfo.getDestinationLongitude()));

        destinations = MapUtils.sortDestinationsByDistance(sourceLocation, destinations);

        Assert.assertEquals(destinations.get(0).getLatitude(), travelPath.get(1).getLatitude(), 0.0);
        Assert.assertEquals(destinations.get(0).getLongitude(), travelPath.get(1).getLongitude(), 0.0);
        Assert.assertEquals(destinations.get(1).getLatitude(), travelPath.get(2).getLatitude(), 0.0);
        Assert.assertEquals(destinations.get(1).getLongitude(), travelPath.get(2).getLongitude(), 0.0);
        Assert.assertEquals(destinations.get(2).getLatitude(), travelPath.get(3).getLatitude(), 0.0);
        Assert.assertEquals(destinations.get(2).getLongitude(), travelPath.get(3).getLongitude(), 0.0);
    }

    @Test
    public void verifyListOfFilteredPeerTravellersAndTravelPathWhenOwnTravellerIsNotGroupOwner(){

        LocalDateTime requestTime = LocalDateTime.now();

        //Own Traveller should not be selected as group owner
        setupOwnTravellerInfo(requestTime.plusSeconds(10));
        Mockito.when(mockTravellerInfo.getModeOfTravel()).thenReturn("Walk");

        HashMap<Integer, TravellerInfo> peerTravellers = getPeerTravellers(requestTime);

        Mockito.when(mockFellowTravellersCache.getFellowTravellers()).thenReturn(peerTravellers);

        testObjectPeerToPeerFragmentViewModel.setFilteredPeerTravellers();

        ArrayList<TravellerInfo> filteredTravellers = testObjectPeerToPeerFragmentViewModel.getFilteredPeerTravellers();

        //Verify only travellers within 1 km distance are selected
        Assert.assertEquals(2, filteredTravellers.size());
        Assert.assertEquals(peerTravellers.get(2).getUserId(), filteredTravellers.get(0).getUserId());
        Assert.assertEquals(peerTravellers.get(3).getUserId(), filteredTravellers.get(1).getUserId());

        Assert.assertFalse(testObjectPeerToPeerFragmentViewModel.getAmIGroupOwner());

        ArrayList<LatLng> travelPath = testObjectPeerToPeerFragmentViewModel.getTravelPath();

        Assert.assertEquals(5, travelPath.size());
        Assert.assertEquals(mockTravellerInfo.getSourceLatitude(), travelPath.get(0).getLatitude(), 0.0);
        Assert.assertEquals(mockTravellerInfo.getSourceLongitude(), travelPath.get(0).getLongitude(), 0.0);

        LatLng groupOwnerSourceLocation = new LatLng(peerTravellers.get(2).getSourceLatitude(), peerTravellers.get(2).getSourceLongitude());

        ArrayList<LatLng> destinations = new ArrayList<>();
        destinations.add(new LatLng(peerTravellers.get(2).getDestinationLatitude(), peerTravellers.get(2).getDestinationLongitude()));
        destinations.add(new LatLng(peerTravellers.get(3).getDestinationLatitude(), peerTravellers.get(3).getDestinationLongitude()));
        destinations.add(new LatLng(mockTravellerInfo.getDestinationLatitude(), mockTravellerInfo.getDestinationLongitude()));

        destinations = MapUtils.sortDestinationsByDistance(groupOwnerSourceLocation, destinations);

        //verify group owners source is added as first destination for own traveller
        Assert.assertEquals(groupOwnerSourceLocation.getLatitude(), travelPath.get(1).getLatitude(), 0.0);
        Assert.assertEquals(groupOwnerSourceLocation.getLongitude(), travelPath.get(1).getLongitude(), 0.0);

        Assert.assertEquals(destinations.get(0).getLatitude(), travelPath.get(2).getLatitude(), 0.0);
        Assert.assertEquals(destinations.get(0).getLongitude(), travelPath.get(2).getLongitude(), 0.0);
        Assert.assertEquals(destinations.get(1).getLatitude(), travelPath.get(3).getLatitude(), 0.0);
        Assert.assertEquals(destinations.get(1).getLongitude(), travelPath.get(3).getLongitude(), 0.0);

        Assert.assertEquals(destinations.get(2).getLatitude(), travelPath.get(4).getLatitude(), 0.0);
        Assert.assertEquals(destinations.get(2).getLongitude(), travelPath.get(4).getLongitude(), 0.0);
    }

    @Test
    public void verifyResetFilteredPeerTravellers() {

        LocalDateTime requestTime = LocalDateTime.now();
        setupOwnTravellerInfo(requestTime.minusSeconds(2));
        Mockito.when(mockTravellerInfo.getModeOfTravel()).thenReturn("Walk");

        HashMap<Integer, TravellerInfo> peerTravellers = getPeerTravellers(requestTime);

        Mockito.when(mockFellowTravellersCache.getFellowTravellers()).thenReturn(peerTravellers);

        testObjectPeerToPeerFragmentViewModel.setFilteredPeerTravellers();

        ArrayList<TravellerInfo> filteredTravellers = testObjectPeerToPeerFragmentViewModel.getFilteredPeerTravellers();

        //Verify only travellers within 1 km distance are selected
        Assert.assertEquals(2, filteredTravellers.size());

        testObjectPeerToPeerFragmentViewModel.resetFilteredPeerTravellers();

        Assert.assertEquals(0, testObjectPeerToPeerFragmentViewModel.getFilteredPeerTravellers().size());
    }

    @Test
    public void verifyListOfFilteredPeerTravellersAsPerUsersPreferredModeOfTravel(){

        LocalDateTime requestTime = LocalDateTime.now();
        setupOwnTravellerInfo(requestTime.minusSeconds(2));
        Mockito.when(mockTravellerInfo.getModeOfTravel()).thenReturn("Walk");

        HashMap<Integer, TravellerInfo> peerTravellers = getPeerTravellers(requestTime);

        peerTravellers.get(2).setModeOfTravel("Walk");
        //Peer with non-matching mode of travel
        peerTravellers.get(3).setModeOfTravel("Car");
        //Peer with destination outside 1 km range
        peerTravellers.get(4).setModeOfTravel("Car");

        Mockito.when(mockFellowTravellersCache.getFellowTravellers()).thenReturn(peerTravellers);

        testObjectPeerToPeerFragmentViewModel.setFilteredPeerTravellers();

        ArrayList<TravellerInfo> filteredTravellers = testObjectPeerToPeerFragmentViewModel.getFilteredPeerTravellers();

        //Verify only travellers within 1 km distance are selected
        Assert.assertEquals(1, filteredTravellers.size());
        Assert.assertEquals(peerTravellers.get(2).getUserId(), filteredTravellers.get(0).getUserId());
    }

    @Test
    public void verifyListOfFilteredPeerTravellersAsPerUsersPreferredGender(){

        LocalDateTime requestTime = LocalDateTime.now();
        setupOwnTravellerInfo(requestTime.minusSeconds(2));
        Mockito.when(mockTravellerInfo.getModeOfTravel()).thenReturn("Walk");

        UserInfo userInfo = getDummyUserInfo();
        userInfo.setPref_gender(Gender.FEMALE.idNumber);
        testObjectPeerToPeerFragmentViewModel.initializeOwnTraveller(userInfo, "");

        HashMap<Integer, TravellerInfo> peerTravellers = getPeerTravellers(requestTime);

        //Peer with non-matching preferred Gender
        peerTravellers.get(2).setModeOfTravel("Walk");
        peerTravellers.get(2).setGender(Gender.MALE);

        peerTravellers.get(3).setModeOfTravel("Walk");
        peerTravellers.get(3).setGender(Gender.FEMALE);

        //Peer with destination outside 1 km range
        peerTravellers.get(4).setModeOfTravel("Walk");
        peerTravellers.get(4).setGender(Gender.FEMALE);

        Mockito.when(mockFellowTravellersCache.getFellowTravellers()).thenReturn(peerTravellers);

        testObjectPeerToPeerFragmentViewModel.setFilteredPeerTravellers();

        ArrayList<TravellerInfo> filteredTravellers = testObjectPeerToPeerFragmentViewModel.getFilteredPeerTravellers();

        //Verify only travellers within 1 km distance are selected
        Assert.assertEquals(1, filteredTravellers.size());
        Assert.assertEquals(peerTravellers.get(3).getUserId(), filteredTravellers.get(0).getUserId());
    }

    private void setupOwnTravellerInfo(LocalDateTime requestStartTime){

        Double sourceLat = 53.343191;
        Double sourceLong = -6.281184;

        Mockito.when(mockTravellerInfo.getRequestStartTime()).thenReturn(requestStartTime);
        Mockito.when(mockTravellerInfo.getSourceLatitude()).thenReturn(sourceLat);
        Mockito.when(mockTravellerInfo.getSourceLongitude()).thenReturn(sourceLong);
        Mockito.when(mockTravellerInfo.getDestinationLatitude()).thenReturn(53.333258);
        Mockito.when(mockTravellerInfo.getDestinationLongitude()).thenReturn(-6.254298);
    }

    private HashMap<Integer, TravellerInfo> getPeerTravellers(LocalDateTime firstRequestTime){

        Double sourceLat = 53.343191;
        Double sourceLong = -6.281184;

        TravellerInfo firstTraveller = new TravellerInfo();
        firstTraveller.setUserId(2);
        firstTraveller.setDestinationLatitude(53.329791);
        firstTraveller.setDestinationLongitude(-6.259427);
        firstTraveller.setSourceLatitude(sourceLat);
        firstTraveller.setSourceLongitude(sourceLong);
        firstTraveller.setRequestStartTime(firstRequestTime);
        firstTraveller.setModeOfTravel("walk");
        firstTraveller.setGender(Gender.MALE);

        TravellerInfo secondTraveller = new TravellerInfo();
        secondTraveller.setUserId(3);
        secondTraveller.setDestinationLatitude(53.333785);
        secondTraveller.setDestinationLongitude(-6.264978);
        secondTraveller.setSourceLatitude(sourceLat);
        secondTraveller.setSourceLongitude(sourceLong);
        secondTraveller.setRequestStartTime(firstRequestTime.plusSeconds(2));
        secondTraveller.setModeOfTravel("walk");
        secondTraveller.setGender(Gender.MALE);

        //PeerTraveller with destination outside 1 km acceptable range
        TravellerInfo thirdTraveller = new TravellerInfo();
        thirdTraveller.setUserId(4);
        thirdTraveller.setDestinationLatitude(53.309057);
        thirdTraveller.setDestinationLongitude(-6.282412);
        thirdTraveller.setSourceLatitude(sourceLat);
        thirdTraveller.setSourceLongitude(sourceLong);
        thirdTraveller.setRequestStartTime(firstRequestTime.plusSeconds(4));
        thirdTraveller.setModeOfTravel("walk");
        thirdTraveller.setGender(Gender.MALE);

        HashMap<Integer, TravellerInfo> peerTravellers = new HashMap<>();
        peerTravellers.put(firstTraveller.getUserId(), firstTraveller);
        peerTravellers.put(secondTraveller.getUserId(), secondTraveller);
        peerTravellers.put(thirdTraveller.getUserId(), thirdTraveller);

        return peerTravellers;
    }

    private UserInfo getDummyUserInfo(){
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfo.setUsername("TestUser");
        userInfo.setAge(25);
        userInfo.setGender("MALE");
        userInfo.setRating(1.2);

        return userInfo;
    }
}
