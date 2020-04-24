package com.tcd.yaatra.extendedFragments;

import com.tcd.yaatra.ui.fragments.DailyFragment;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;

public class ExtendedDailyCommuteFragment extends DailyFragment {

    DailyCommuteActivityViewModel mockViewModel;

    public ExtendedDailyCommuteFragment(){

    }

    public ExtendedDailyCommuteFragment(DailyCommuteActivityViewModel viewModel){
        mockViewModel = viewModel;
    }

    @Override
    protected void configureDagger() {
        super.configureDagger();

        //Set mock dependencies
        this.dailyCommuteActivityViewModel = mockViewModel;
    }
}
