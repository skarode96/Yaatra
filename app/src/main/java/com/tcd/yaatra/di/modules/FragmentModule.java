package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.ui.activities.DailyFragment;
import com.tcd.yaatra.ui.activities.MapFragment;
import com.tcd.yaatra.ui.activities.MapBoxInputFragment;
import com.tcd.yaatra.ui.fragments.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface FragmentModule {

    @ContributesAndroidInjector
    DailyFragment contributeDailyFragment();

    @ContributesAndroidInjector
    MapBoxInputFragment contributeMapBoxInputFragment();

    @ContributesAndroidInjector
    SettingsFragment contributeSettingsFragment();

    @ContributesAndroidInjector
    MapFragment contributeMapActivity();
}
