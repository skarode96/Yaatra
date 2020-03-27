package com.tcd.yaatra;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.extendedFragments.ExtendedPeerToPeerFragment;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;

public class CustomFragmentFactory extends FragmentFactory {

    PeerToPeerFragmentViewModel peerToPeerFragmentViewModel;
    PeerCommunicator peerCommunicator;
    UserInfoRepository userInfoRepository;

    public CustomFragmentFactory(PeerToPeerFragmentViewModel viewModel, UserInfoRepository repository, PeerCommunicator communicator){
        peerToPeerFragmentViewModel = viewModel;
        peerCommunicator = communicator;
        userInfoRepository = repository;
    }

    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {

        if(className == ExtendedPeerToPeerFragment.class.getName()){
            return new ExtendedPeerToPeerFragment(peerToPeerFragmentViewModel, userInfoRepository, peerCommunicator);
        }

        return super.instantiate(classLoader, className);
    }
}
