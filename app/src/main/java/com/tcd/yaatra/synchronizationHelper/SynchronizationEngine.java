package com.tcd.yaatra.synchronizationHelper;

import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.google.gson.Gson;
import com.tcd.yaatra.repository.datasource.RatingRepository;
import com.tcd.yaatra.services.api.yaatra.models.Rating;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class SynchronizationEngine {

    @Inject
    RatingRepository ratingRepository;

    @Inject
    public SynchronizationEngine(){}

    public void scheduleSynchronization(){

        ratingRepository.getRatings().observeForever(new Observer<List<Rating>>() {
            @Override
            public void onChanged(List<Rating> ratings) {
                if(ratings != null && ratings.size() > 0){

                    Constraints constraints = new Constraints.Builder().setRequiresBatteryNotLow(true)
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                    OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(RatingSyncWorker.class)
                            .setInputData(createInputData(ratings))
                            .setConstraints(constraints)
                            .setInitialDelay(5, TimeUnit.SECONDS).build();

                    WorkManager workManager = WorkManager.getInstance();
                    workManager.enqueue(oneTimeWorkRequest);
                }
            }
        });
    }

    private Data createInputData(List<Rating> ratings){

        Gson gson = new Gson();
        Data data = new Data.Builder().putString("ratings", gson.toJson(ratings)).build();
        return data;
    }
}
