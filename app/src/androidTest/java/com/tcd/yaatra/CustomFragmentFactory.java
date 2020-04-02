package com.tcd.yaatra;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.extendedFragments.ExtendedPeerToPeerFragment;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;

public class CustomFragmentFactory extends FragmentFactory {

    PeerToPeerFragmentViewModel peerToPeerFragmentViewModel;
    PeerCommunicator peerCommunicator;
    UserInfoRepository userInfoRepository;
    FragmentManager fragmentManager;

    public CustomFragmentFactory(PeerToPeerFragmentViewModel viewModel, UserInfoRepository repository
            , PeerCommunicator communicator, FragmentManager manager){
        peerToPeerFragmentViewModel = viewModel;
        peerCommunicator = communicator;
        userInfoRepository = repository;
        fragmentManager = manager;
    }

    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {

        if(className == ExtendedPeerToPeerFragment.class.getName()){
            return new ExtendedPeerToPeerFragment(peerToPeerFragmentViewModel, userInfoRepository, peerCommunicator, fragmentManager);
        }

        return super.instantiate(classLoader, className);
    }
}
