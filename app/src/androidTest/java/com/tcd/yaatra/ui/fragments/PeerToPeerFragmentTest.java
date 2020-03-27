package com.tcd.yaatra.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.tcd.yaatra.CustomFragmentFactory;
import com.tcd.yaatra.R;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.extendedFragments.ExtendedPeerToPeerFragment;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
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

    String testUserName = "testUser";

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        SharedPreferenceUtils.setUserName(testUserName);

        LiveData<UserInfo> userInfoLiveData = new LiveData<UserInfo>() {
            @Override
            public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super UserInfo> observer) {
                super.observe(owner, observer);
            }
        };
        Mockito.when(userInfoRepository.getUserProfile(testUserName)).thenReturn(userInfoLiveData);

        CustomFragmentFactory customFragmentFactory = new CustomFragmentFactory(peerToPeerFragmentViewModel, userInfoRepository, peerCommunicator);
        testObjectPeerToPeerFragment = FragmentScenario.launchInContainer(ExtendedPeerToPeerFragment.class, new Bundle(), R.style.Theme_AppCompat, customFragmentFactory);
    }

    @Test
    public void testInitialize() {

        verify(peerToPeerFragmentViewModel, Mockito.times(1)).setIsInitialized(false);
        verify(userInfoRepository, Mockito.times(1)).getUserProfile(testUserName);

    }
}

