package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.synchronizationHelper.RatingSyncChildWorkerFactory;
import com.tcd.yaatra.synchronizationHelper.RatingSyncWorker;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(RatingSyncWorker.class)
    RatingSyncChildWorkerFactory bindRatingSyncWorker(RatingSyncWorker.Factory factory);
}
