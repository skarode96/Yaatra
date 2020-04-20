package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.repository.DailyCommuteRepository;
import com.tcd.yaatra.repository.UserRatingRepository;
import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.services.api.yaatra.api.RatingApi;
import com.tcd.yaatra.services.api.yaatra.api.ScheduleDailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteDetailsApi;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;
import com.tcd.yaatra.services.api.yaatra.api.RegisterApi;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    UserRepository providesUserRepository(LoginApi loginApi, RegisterApi registerApi){
        return new UserRepository(loginApi, registerApi);
    }

    @Provides
    DailyCommuteRepository providesDailyCommuteRepository(DailyCommuteApi dailyCommuteApi, ScheduleDailyCommuteApi scheduleDailyCommuteApi, DailyCommuteDetailsApi dailyCommuteDetailsApi){
        return new DailyCommuteRepository(dailyCommuteApi, scheduleDailyCommuteApi, dailyCommuteDetailsApi);
    }

    @Provides
    UserRatingRepository providesUserRatingRepository(RatingApi ratingApi){
        return new UserRatingRepository(ratingApi);
    }

}
