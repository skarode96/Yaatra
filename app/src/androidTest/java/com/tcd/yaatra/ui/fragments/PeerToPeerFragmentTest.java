package com.tcd.yaatra.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tcd.yaatra.CustomFragmentFactory;
import com.tcd.yaatra.R;
import com.tcd.yaatra.RecyclerViewMatcher;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.extendedFragments.ExtendedPeerToPeerFragment;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.repository.models.TravellerStatus;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;
import com.tcd.yaatra.utils.Constants;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PeerToPeerFragmentTest {

    FragmentScenario<ExtendedPeerToPeerFragment> testObjectPeerToPeerFragment;

    @Mock
    UserInfoRepository userInfoRepository;

    @Mock
    PeerToPeerFragmentViewModel peerToPeerFragmentViewModel;

    @Mock
    PeerCommunicator peerCommunicator;

    @Mock
    FragmentManager mockFragmentManager;

    @Mock
    FragmentTransaction fragmentTransaction;

    String testUserName = "testUser";

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        if(SharedPreferenceUtils.getUserName() == null){
            SharedPreferenceUtils.setUserName(testUserName);
        }
        else {
            testUserName = SharedPreferenceUtils.getUserName();
        }

        LiveData<UserInfo> userInfoLiveData = new LiveData<UserInfo>() {
            @Override
            public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super UserInfo> observer) {
                super.observe(owner, observer);
            }
        };
        Mockito.when(userInfoRepository.getUserProfile(testUserName)).thenReturn(userInfoLiveData);

        Mockito.when(peerToPeerFragmentViewModel.getIsInitialized()).thenReturn(true);

        CustomFragmentFactory customFragmentFactory = new CustomFragmentFactory(peerToPeerFragmentViewModel
                                                        , userInfoRepository, peerCommunicator, mockFragmentManager);
        testObjectPeerToPeerFragment = FragmentScenario.launchInContainer(ExtendedPeerToPeerFragment.class
                                            , new Bundle(), R.style.Theme_AppCompat, customFragmentFactory);
    }

    @Test
    public void testInitializeStartsStatusBroadcast() {

        verify(peerToPeerFragmentViewModel).setIsInitialized(false);
        verify(userInfoRepository).getUserProfile(testUserName);
        verify(peerCommunicator).initialize(Mockito.any(PeerToPeerFragment.class));
        verify(peerCommunicator).broadcastTravellers(TravellerStatus.SeekingFellowTraveller);
    }

    @Test
    public void testClickOfStartNavigationAfterPeerSearchIsCompleted(){

        onView(withId(R.id.startNavigation)).check(matches(not(isDisplayed())));

        //Wait for the duration of peer search
        try {
            Thread.sleep(Constants.SCAN_DURATION_IN_MILLISECONDS + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.startNavigation)).check(matches(isDisplayed()));
        onView(withId(R.id.startNavigation)).check(matches(isEnabled()));

        ArrayList<LatLng> travelPath = new ArrayList<>();
        ArrayList<TravellerInfo> filteredPeerTravellers = new ArrayList<>();

        Mockito.when(peerToPeerFragmentViewModel.getTravelPath()).thenReturn(travelPath);
        Mockito.when(peerToPeerFragmentViewModel.getFilteredPeerTravellers()).thenReturn(filteredPeerTravellers);

        Mockito.when(mockFragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        Mockito.when(fragmentTransaction.replace(eq(R.id.fragment_container), Mockito.any(RouteInfoFragment.class))).thenReturn(fragmentTransaction);
        Mockito.when(fragmentTransaction.addToBackStack("peerFrag")).thenReturn(fragmentTransaction);

        onView(withId(R.id.startNavigation)).perform(click());

        verify(peerCommunicator).broadcastTravellers(TravellerStatus.TravellingToStartPoint);
        verify(peerToPeerFragmentViewModel).getTravelPath();
        verify(peerToPeerFragmentViewModel, Mockito.atLeastOnce()).getFilteredPeerTravellers();
        verify(fragmentTransaction).commit();
        verify(fragmentTransaction);
    }

    @Test
    public void testPeerTravellersAreShownInList(){

        ArrayList<TravellerInfo> peerTravellers = getPeerTravellers();
        Mockito.when(peerToPeerFragmentViewModel.getFilteredPeerTravellers()).thenReturn(peerTravellers);

        testObjectPeerToPeerFragment.onFragment(fragment -> {
            fragment.processFellowTravellersInfo();
        });

        //Wait for the duration of peer search
        try {
            Thread.sleep(Constants.SCAN_DURATION_IN_MILLISECONDS + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        checkIfPeerTravellersAreDisplayed(peerTravellers);
    }

    private void checkIfPeerTravellersAreDisplayed(ArrayList<TravellerInfo> travellers) {

        int position = 0;
        for(TravellerInfo travellerInfo : travellers){

            onView(withRecyclerView(R.id.peer_recycler_view)
                    .atPosition(position))
                    .check(matches((hasDescendant(withText(travellerInfo.getUserName())))));
            onView(withRecyclerView(R.id.peer_recycler_view)
                    .atPosition(position))
                    .check(matches((hasDescendant(withText(travellerInfo.getStatus().toString())))));
            onView(withRecyclerView(R.id.peer_recycler_view)
                    .atPosition(position))
                    .check(matches((hasDescendant(withText(travellerInfo.getModeOfTravel())))));
            onView(withRecyclerView(R.id.peer_recycler_view)
                    .atPosition(position))
                    .check(matches((hasDescendant(withText(travellerInfo.getSourceName())))));
            onView(withRecyclerView(R.id.peer_recycler_view)
                    .atPosition(position))
                    .check(matches((hasDescendant(withText(travellerInfo.getDestinationName())))));

            position++;
        }
    }

    private ArrayList<TravellerInfo> getPeerTravellers(){

        TravellerInfo traveller1 = new TravellerInfo();
        traveller1.setUserId(1);
        traveller1.setUserName("Traveller1");
        traveller1.setSourceName("Source1");
        traveller1.setDestinationName("Dest1");
        traveller1.setModeOfTravel("Walking");
        traveller1.setGender(Gender.MALE);
        traveller1.setStatus(TravellerStatus.SeekingFellowTraveller);

        TravellerInfo traveller2 = new TravellerInfo();
        traveller2.setUserId(2);
        traveller2.setUserName("Traveller2");
        traveller2.setSourceName("Source2");
        traveller2.setDestinationName("Dest2");
        traveller2.setModeOfTravel("Walking");
        traveller2.setGender(Gender.FEMALE);
        traveller2.setStatus(TravellerStatus.TravellingToStartPoint);

        ArrayList<TravellerInfo> travellers = new ArrayList<>();
        travellers.add(traveller1);
        travellers.add(traveller2);

        return travellers;
    }

    private static RecyclerViewMatcher withRecyclerView(int id){
        return new RecyclerViewMatcher(id);
    }
}

