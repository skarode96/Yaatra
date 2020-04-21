package com.tcd.yaatra.synchronizationHelper;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import com.tcd.yaatra.utils.FactoryUtils;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

//Source: https://stackoverflow.com/questions/56545821/inject-classes-into-workmanager-with-dagger2-java
public class RatingSyncWorkerFactory extends WorkerFactory {

    private final Map<Class<? extends ListenableWorker>, Provider<RatingSyncChildWorkerFactory>> workersFactories;

    @Inject
    public RatingSyncWorkerFactory(Map<Class<? extends ListenableWorker>, Provider<RatingSyncChildWorkerFactory>> workersFactories) {
        this.workersFactories = workersFactories;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
        Provider<RatingSyncChildWorkerFactory> factoryProvider = FactoryUtils.getWorkerFactoryProviderByKey(workersFactories, workerClassName);
        return factoryProvider.get().create(appContext, workerParameters);
    }
}
