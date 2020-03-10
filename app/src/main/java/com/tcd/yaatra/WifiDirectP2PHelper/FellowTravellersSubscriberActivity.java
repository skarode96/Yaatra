package com.tcd.yaatra.WifiDirectP2PHelper;

import com.tcd.yaatra.databinding.ActivityPeerToPeerBinding;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.ui.activities.BaseActivity;
import java.util.HashMap;

public abstract class FellowTravellersSubscriberActivity extends BaseActivity<ActivityPeerToPeerBinding> {

    protected abstract void processFellowTravellersInfo(HashMap<Integer, TravellerInfo> fellowTravellers);

}
