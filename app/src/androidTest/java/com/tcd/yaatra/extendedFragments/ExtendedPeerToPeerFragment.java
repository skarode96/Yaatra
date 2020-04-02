package com.tcd.yaatra.extendedFragments;

import android.view.View;
import androidx.fragment.app.FragmentManager;
import com.tcd.yaatra.WifiDirectP2PHelper.PeerCommunicator;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.ui.fragments.PeerToPeerFragment;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;

public class ExtendedPeerToPeerFragment extends PeerToPeerFragment {

    PeerToPeerFragmentViewModel mockViewModel;
    PeerCommunicator mockCommunicator;
    UserInfoRepository mockRepository;
    FragmentManager mockFragmentManager;

    public ExtendedPeerToPeerFragment(){

    }

    public ExtendedPeerToPeerFragment(PeerToPeerFragmentViewModel viewModel, UserInfoRepository repository
            , PeerCommunicator communicator, FragmentManager manager){
        mockViewModel = viewModel;
        mockCommunicator = communicator;
        mockRepository = repository;
        mockFragmentManager = manager;
    }

    @Override
    protected void configureDagger() {
        super.configureDagger();

        //Set mock dependencies
        this.peerToPeerFragmentViewModel = mockViewModel;
        this.userInfoRepository = mockRepository;
        this.peerCommunicator = mockCommunicator;
    }

    @Override
    public void onResume() {
        fragmentManager = mockFragmentManager;

        //Disable animation for testing
        layoutDataBinding.loader.setVisibility(View.GONE);
        super.onResume();
    }
}
