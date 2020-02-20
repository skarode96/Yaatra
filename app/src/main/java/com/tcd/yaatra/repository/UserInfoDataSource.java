package com.tcd.yaatra.repository;

import com.tcd.yaatra.repository.dao.UserInfoDao;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;

public class UserInfoDataSource implements UserInfoRepository {
    private UserInfoDao userInfoDao;

    @Inject
    public UserInfoDataSource(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }

    @Override
    public LiveData<UserInfo> getUserProfile(String userName) {
        return userInfoDao.getUserProfile(userName);
    }

    @Override
    public void insertUser(UserInfo user) {
        userInfoDao.insertUser(user);
    }

    @Override
    public void updateUser(UserInfo user) {
        userInfoDao.updateUser(user);
    }

    @Override
    public void deleteUser(String userName) {
        userInfoDao.deleteUser(userName);
    }

    @Override
    public void deleteUsers(UserInfo user) {
        userInfoDao.deleteUsers(user);
    }
}
