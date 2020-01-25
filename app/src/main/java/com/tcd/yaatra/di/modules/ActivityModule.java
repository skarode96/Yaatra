package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.ui.activities.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector
    LoginActivity contributeLoginActivity();
}
