package com.tcd.yaatra.di;

import androidx.lifecycle.ViewModelProvider;

import com.tcd.yaatra.ui.viewmodels.CreateDailyCommuteFragmentViewModel;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.LoginActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.RegisterActivityViewModel;
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
    @ViewModelKey(CreateDailyCommuteFragmentViewModel.class)
    abstract CreateDailyCommuteFragmentViewModel bindCreateDailyCommuteFragmentViewModel(CreateDailyCommuteFragmentViewModel createDailyCommuteFragmentViewModel);

}
