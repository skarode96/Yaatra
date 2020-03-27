package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.extendedFragments.ExtendedPeerToPeerFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ExtendedFragmentModule {

    @ContributesAndroidInjector
    ExtendedPeerToPeerFragment contributeTestPeerToPeerFragment();
}