package com.tcd.yaatra.extendedFragments;

import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.ui.fragments.PeerToPeerFragment;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;

public class ExtendedPeerToPeerFragment extends PeerToPeerFragment {

    PeerToPeerFragmentViewModel mockViewModel;
    PeerCommunicator mockCommunicator;
    UserInfoRepository mockRepository;

    public ExtendedPeerToPeerFragment(){

    }

    public ExtendedPeerToPeerFragment(PeerToPeerFragmentViewModel viewModel, UserInfoRepository repository, PeerCommunicator communicator){
        mockViewModel = viewModel;
        mockCommunicator = communicator;
        mockRepository = repository;
    }

    @Override
    protected void configureDagger() {
        super.configureDagger();
        this.peerToPeerFragmentViewModel = mockViewModel;
        this.userInfoRepository = mockRepository;
        this.peerCommunicator = mockCommunicator;
    }
}
