package com.tcd.yaatra.di.modules;

import android.app.Application;

import com.tcd.yaatra.repository.JourneySharingDatabase;
import com.tcd.yaatra.repository.UserInfoDataSource;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.repository.dao.UserInfoDao;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;

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
}
