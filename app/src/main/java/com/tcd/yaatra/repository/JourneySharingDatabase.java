package com.tcd.yaatra.repository;

import android.content.Context;

import com.tcd.yaatra.repository.dao.UserInfoDao;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = UserInfo.class, exportSchema = false,version = 1)
public abstract class JourneySharingDatabase extends RoomDatabase {
    public abstract UserInfoDao getUserInfoDao();
}
