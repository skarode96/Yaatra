package com.tcd.yaatra;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.extendedFragments.ExtendedDailyCommuteFragment;
import com.tcd.yaatra.extendedFragments.ExtendedPeerToPeerFragment;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;

public class CustomFragmentFactory extends FragmentFactory {

    PeerToPeerFragmentViewModel peerToPeerFragmentViewModel;
    PeerCommunicator peerCommunicator;
    UserInfoRepository userInfoRepository;
    FragmentManager fragmentManager;

    DailyCommuteActivityViewModel dailyCommuteActivityViewModel;

    public CustomFragmentFactory(PeerToPeerFragmentViewModel viewModel, UserInfoRepository repository
            , PeerCommunicator communicator, FragmentManager manager){
        peerToPeerFragmentViewModel = viewModel;
        peerCommunicator = communicator;
        userInfoRepository = repository;
        fragmentManager = manager;
    }

    public CustomFragmentFactory(DailyCommuteActivityViewModel viewModel){
        dailyCommuteActivityViewModel = viewModel;
    }

    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {

        if(className == ExtendedPeerToPeerFragment.class.getName()){
            return new ExtendedPeerToPeerFragment(peerToPeerFragmentViewModel, userInfoRepository, peerCommunicator, fragmentManager);
        }
        else if(className == ExtendedDailyCommuteFragment.class.getName()){
            return new ExtendedDailyCommuteFragment(dailyCommuteActivityViewModel);
        }

        return super.instantiate(classLoader, className);
    }
}
