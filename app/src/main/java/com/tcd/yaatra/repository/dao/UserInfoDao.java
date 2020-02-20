package com.tcd.yaatra.repository.dao;

import com.tcd.yaatra.services.api.yaatra.models.UserInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserInfoDao {

    @Query("Select * from userInfo")
    List<UserInfo> getUserList();

    @Query("Select * from userInfo where username = :userName")
    LiveData<UserInfo> getUserProfile(String userName);

    @Insert
    void insertUser(UserInfo user);

    @Update
    void updateUser(UserInfo user);

    @Query("DELETE FROM userInfo WHERE username = :userName")
    void deleteUser(String userName);

    @Delete
    void deleteUsers(UserInfo user);
}
