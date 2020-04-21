package com.tcd.yaatra.utils;

import androidx.work.ListenableWorker;
import com.tcd.yaatra.synchronizationHelper.RatingSyncChildWorkerFactory;
import java.util.Map;
import java.util.Objects;
import javax.inject.Provider;

public class FactoryUtils {
    /**
     *
     * @param map workers
     * @param key workers name (class name)
     * @return
     */
    public static Provider<RatingSyncChildWorkerFactory> getWorkerFactoryProviderByKey(Map<Class<? extends ListenableWorker>, Provider<RatingSyncChildWorkerFactory>> map, String key) {
        for (Map.Entry<Class<? extends ListenableWorker>, Provider<RatingSyncChildWorkerFactory>> entry : map.entrySet()) {
            if (Objects.equals(key, entry.getKey().getName())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
