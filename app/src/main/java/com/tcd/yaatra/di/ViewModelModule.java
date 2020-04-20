package com.tcd.yaatra.di;

import androidx.lifecycle.ViewModelProvider;

import com.tcd.yaatra.ui.viewmodels.ScheduleDailyCommuteFragmentViewModel;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteDetailsViewModel;
import com.tcd.yaatra.ui.viewmodels.LoginActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.PeerToPeerFragmentViewModel;
import com.tcd.yaatra.ui.viewmodels.RegisterActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.UserRatingActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.ViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(LoginActivityViewModel.class)
    abstract LoginActivityViewModel bindLoginActivityViewModel(LoginActivityViewModel loginActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DailyCommuteActivityViewModel.class)
    abstract DailyCommuteActivityViewModel bindDailyCommuteActivityViewModel(DailyCommuteActivityViewModel dailyCommuteActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RegisterActivityViewModel.class)
    abstract RegisterActivityViewModel bindRegisterActivityViewModel(RegisterActivityViewModel registerActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ScheduleDailyCommuteFragmentViewModel.class)
    abstract ScheduleDailyCommuteFragmentViewModel bindCreateDailyCommuteFragmentViewModel(ScheduleDailyCommuteFragmentViewModel scheduleDailyCommuteFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ScheduleDailyCommuteFragmentViewModel.class)
    abstract DailyCommuteDetailsViewModel bindDailyCommuteDetailsViewModel(DailyCommuteDetailsViewModel dailyCommuteDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PeerToPeerFragmentViewModel.class)
    abstract PeerToPeerFragmentViewModel bindPeerToPeerFragmentViewModel(PeerToPeerFragmentViewModel peerToPeerFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserRatingActivityViewModel.class)
    abstract UserRatingActivityViewModel bindUserRatingActivityViewModel(UserRatingActivityViewModel userRatingActivityViewModel);
}
