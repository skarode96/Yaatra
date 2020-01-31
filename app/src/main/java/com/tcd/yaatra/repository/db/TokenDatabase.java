package com.tcd.yaatra.repository.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tcd.yaatra.repository.dao.TokenDaoAccess;
import com.tcd.yaatra.repository.entity.Token;

@Database(entities = {Token.class}, version = 1)
public abstract class TokenDatabase extends RoomDatabase {
    public abstract TokenDaoAccess tokenDaoAccess();
}
