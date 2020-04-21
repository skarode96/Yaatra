package com.tcd.yaatra.synchronizationHelper;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.gson.Gson;
import com.tcd.yaatra.repository.datasource.RatingRepository;
import com.tcd.yaatra.services.api.yaatra.models.RateRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.Rating;
import com.tcd.yaatra.ui.viewmodels.UserRatingFragmentViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;

public class RatingSyncWorker extends Worker {

    private UserRatingFragmentViewModel userRatingFragmentViewModel;
    private RatingRepository ratingRepository;

    public RatingSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams
            , UserRatingFragmentViewModel viewModel
            , RatingRepository repository) {
        super(context, workerParams);
        this.userRatingFragmentViewModel = viewModel;
        this.ratingRepository = repository;
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {

        Log.e("RatingSyncWorker","Ratings Sync Started");

        Gson gson = new Gson();
        List<Rating> ratings = new ArrayList<>(Arrays.asList(gson.fromJson(getInputData().getString("ratings"), Rating[].class)));

        for(Rating rating: ratings) {
            saveData(rating);
        }

        Log.e("RatingSyncWorker","Ratings Synced Successfully");

        return ListenableWorker.Result.success();
    }

    private void saveData(final Rating rating){

        final RateRequestBody rateRequestBody = new RateRequestBody();
        rateRequestBody.setUserName(rating.getUsername());
        rateRequestBody.setRating(rating.getValue());

        userRatingFragmentViewModel.rate(rateRequestBody);
        ratingRepository.deleteRating(rating);
    }


    public static class Factory implements RatingSyncChildWorkerFactory {

        private final Provider<UserRatingFragmentViewModel> modelProvider;
        private final Provider<RatingRepository> repositoryProvider;

        @Inject
        public Factory(Provider<UserRatingFragmentViewModel> modelProvider, Provider<RatingRepository> repositoryProvider) {
            this.modelProvider = modelProvider;
            this.repositoryProvider = repositoryProvider;
        }

        @Override
        public ListenableWorker create(Context context, WorkerParameters workerParameters) {
            return new RatingSyncWorker(context,
                    workerParameters,
                    modelProvider.get(),
                    repositoryProvider.get());
        }
    }
}
