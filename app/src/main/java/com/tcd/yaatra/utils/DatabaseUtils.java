package com.tcd.yaatra.utils;

import android.content.Context;

import com.tcd.yaatra.repository.dao.UserInfoDao;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = UserInfo.class, exportSchema = false,version = 1)
public abstract class DatabaseUtils extends RoomDatabase {
    private static final String DB_NAME = "userInfo";
    private static DatabaseUtils instance;

    public static synchronized DatabaseUtils getInstance(Context context){
        if(instance==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),DatabaseUtils.class,DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract UserInfoDao userInfoDao();
}
