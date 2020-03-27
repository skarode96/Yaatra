package com.tcd.yaatra;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import com.tcd.yaatra.di.modules.DaggerTestAppComponent;
import javax.inject.Inject;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;

public class TestApp extends App {

    private static Context context;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        TestApp.context = this;
        initDagger();
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

        DaggerTestAppComponent.builder().bindApplication(this).build().inject(this);

    }

    public static Context getAppContext() {
        return TestApp.context;
    }
}