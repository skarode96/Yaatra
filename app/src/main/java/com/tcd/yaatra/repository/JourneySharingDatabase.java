package com.tcd.yaatra.repository;

import android.content.Context;

import com.tcd.yaatra.repository.dao.RatingDao;
import com.tcd.yaatra.repository.dao.UserInfoDao;
import com.tcd.yaatra.services.api.yaatra.models.Rating;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserInfo.class, Rating.class}, exportSchema = false,version = 2)
public abstract class JourneySharingDatabase extends RoomDatabase {
    public abstract UserInfoDao getUserInfoDao();

    public abstract RatingDao getRatingDao();
}
