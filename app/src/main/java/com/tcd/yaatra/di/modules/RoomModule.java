package com.tcd.yaatra.di.modules;

import android.app.Application;

import com.tcd.yaatra.repository.JourneySharingDatabase;
import com.tcd.yaatra.repository.dao.RatingDao;
import com.tcd.yaatra.repository.datasource.RatingDataSource;
import com.tcd.yaatra.repository.datasource.RatingRepository;
import com.tcd.yaatra.repository.datasource.UserInfoDataSource;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.repository.dao.UserInfoDao;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    private JourneySharingDatabase journeySharingDatabase;


    @Singleton
    @Provides
    JourneySharingDatabase providesRoomDatabase(Application application){
        journeySharingDatabase = Room.databaseBuilder(application,JourneySharingDatabase.class,"userInfo").fallbackToDestructiveMigration().build();
        return journeySharingDatabase;
    }

    @Singleton
    @Provides
    UserInfoDao providesUserInfoDao(JourneySharingDatabase journeySharingDatabase) {
        return journeySharingDatabase.getUserInfoDao();
    }

    @Singleton
    @Provides
    UserInfoRepository userInfoRepository(UserInfoDao userInfoDao){
        return new UserInfoDataSource(userInfoDao);
    }

    @Singleton
    @Provides
    RatingDao providesRatingDao(JourneySharingDatabase journeySharingDatabase) {
        return journeySharingDatabase.getRatingDao();
    }

    @Singleton
    @Provides
    RatingRepository ratingRepository(RatingDao ratingDao){
        return new RatingDataSource(ratingDao);
    }
}
