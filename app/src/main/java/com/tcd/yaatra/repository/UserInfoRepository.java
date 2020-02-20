package com.tcd.yaatra.repository;

import com.tcd.yaatra.services.api.yaatra.models.UserInfo;

import androidx.lifecycle.LiveData;

public interface UserInfoRepository {
    LiveData<UserInfo> getUserProfile(String userName);
    void insertUser(UserInfo user);
    void updateUser(UserInfo user);
    void deleteUser(String userName);
    void deleteUsers(UserInfo user);
}
