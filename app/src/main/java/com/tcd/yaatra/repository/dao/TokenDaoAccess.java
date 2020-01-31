package com.tcd.yaatra.repository.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.tcd.yaatra.repository.entity.Token;

@Dao
public interface TokenDaoAccess {
    @Insert
    boolean insertToken(String token);

    @Query("SELECT * FROM Token")
    LiveData<Token> getToken();

    @Delete
    boolean deleteToken();
}
