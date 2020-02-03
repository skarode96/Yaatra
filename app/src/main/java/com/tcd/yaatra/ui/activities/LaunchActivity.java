package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityLaunchBinding;

public class LaunchActivity extends BaseActivity<ActivityLaunchBinding> {

    SharedPreferences preferences;
    String savedToken;

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.preferences = getSharedPreferences(); // 0 - for private mode
        this.savedToken = getToken();
        handleLaunch();

    }

    private void handleLaunch() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(!savedToken.equals("no token")) {
                Intent myIntent = new Intent(LaunchActivity.this, DailyCommuteActivity.class);
                startActivity(myIntent);
            } else {
                Intent myIntent = new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
            finish();
        },2000);
    }

    private String getToken() {
        return this.preferences.getString("token", "no token");
    }

    private SharedPreferences getSharedPreferences() {
        return getApplicationContext().getSharedPreferences("LoginPref", 0);
    }


}
