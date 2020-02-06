package com.tcd.yaatra.utils;

import android.content.SharedPreferences;

import com.tcd.yaatra.App;

public class SharedPreferenceUtils {

    private static final String LOGIN_PREF = "LoginPref";
    private static final String TOKEN_KEY = "token";
    private static final String TOKEN_PREFIX = "Token ";
    public static final String DEFAULT_TOKEN = "default";

    public static SharedPreferences createSharedPreference(String key, Integer mode) {
        SharedPreferences sharedPreferences = App.getAppContext().getSharedPreferences(key, mode);
        return sharedPreferences;
    }

    public static SharedPreferences createLoginSharedPreference() {
        SharedPreferences loginPref = createSharedPreference(LOGIN_PREF, 0);// 0 for private mode
        return loginPref;
    }

    public static SharedPreferences getLoginSharedPreference() {
        return App.getAppContext().getSharedPreferences(LOGIN_PREF, 0);
    }

    public static boolean setAuthToken(String token) {
        getLoginSharedPreference().edit().putString(TOKEN_KEY, TOKEN_PREFIX + token).apply();
        return true;
    }

    public static String getAuthToken() {
        return getLoginSharedPreference().getString(TOKEN_KEY, DEFAULT_TOKEN);
    }

    public static boolean clearAuthToken() {
        getLoginSharedPreference().edit().remove(TOKEN_KEY).apply();
        return true;
    }

}
