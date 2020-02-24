package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.ui.activities.LaunchActivity;
import com.tcd.yaatra.ui.activities.LoginActivity;
import com.tcd.yaatra.ui.activities.MenuContainerActivity;
import com.tcd.yaatra.ui.activities.PeerToPeerActivity;
import com.tcd.yaatra.ui.activities.RegisterActivity;
import com.tcd.yaatra.ui.activities.RouteInfo;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    LaunchActivity contributeLaunchActivity();

    @ContributesAndroidInjector
    PeerToPeerActivity contributePeerToPeerActivity();

    @ContributesAndroidInjector
    MenuContainerActivity contributeMenuContainerActivity();

    @ContributesAndroidInjector
    RegisterActivity contributeRegisterActivity();

    @ContributesAndroidInjector
    RouteInfo contributeRouteinfo();


}
