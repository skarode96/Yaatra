package com.tcd.yaatra.synchronizationHelper;

import android.content.Context;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

public interface RatingSyncChildWorkerFactory {
    ListenableWorker create(Context appContext, WorkerParameters workerParameters);
}
