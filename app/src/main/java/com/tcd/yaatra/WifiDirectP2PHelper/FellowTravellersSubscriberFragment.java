package com.tcd.yaatra.WifiDirectP2PHelper;

import androidx.databinding.ViewDataBinding;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.ui.fragments.BaseFragment;

import java.util.HashMap;

public abstract class FellowTravellersSubscriberFragment<DataBindingClass extends ViewDataBinding> extends BaseFragment<DataBindingClass> {

    protected abstract void processFellowTravellersInfo();

}
