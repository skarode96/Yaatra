package com.tcd.yaatra;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tcd.yaatra.di.AppComponent;
import com.tcd.yaatra.di.DaggerAppComponent;
import com.tcd.yaatra.synchronizationHelper.RatingSyncWorkerFactory;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class App extends Application implements HasActivityInjector, HasSupportFragmentInjector {

    private static Context context;
    private static AppComponent component;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        initDagger();
        configureWorkManager();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }

    private void initDagger(){
        component = DaggerAppComponent.builder()
                .bindApplication(this)
                .build();

        component.inject(this);
    }

    public static Context getAppContext() {
        return App.context;
    }

    private void configureWorkManager() {
        RatingSyncWorkerFactory factory = component.ratingSyncWorkerFactory();
        Configuration config = new Configuration.Builder()
                .setWorkerFactory(factory)
                .setMinimumLoggingLevel(Log.DEBUG)
                .build();

        WorkManager.initialize(this, config);
    }
}
