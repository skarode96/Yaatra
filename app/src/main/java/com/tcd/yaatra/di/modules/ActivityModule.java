package com.tcd.yaatra.di.modules;

import android.app.Activity;

import com.tcd.yaatra.ui.activities.DailyCommuteActivity;
import com.tcd.yaatra.ui.activities.LaunchActivity;
import com.tcd.yaatra.ui.activities.LoginActivity;

import com.tcd.yaatra.ui.activities.DailyCommuteListActivity;
import com.tcd.yaatra.ui.activities.PeerToPeerActivity;
import com.tcd.yaatra.ui.activities.RegisterActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    DailyCommuteActivity contributetDailyCommuteActivity();

    @ContributesAndroidInjector
    DailyCommuteListActivity contributeUserActivity();

    @ContributesAndroidInjector
    LaunchActivity contributeLaunchActivity();

    @ContributesAndroidInjector
    PeerToPeerActivity contributePeerToPeerActivity();

    @ContributesAndroidInjector
    RegisterActivity contributeRegisterActivity();
}
