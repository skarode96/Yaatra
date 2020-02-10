package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityLaunchBinding;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

public class LaunchActivity extends BaseActivity<ActivityLaunchBinding> {

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleLaunch();
    }

    private void handleLaunch() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(!SharedPreferenceUtils.getAuthToken().equals(SharedPreferenceUtils.DEFAULT_TOKEN)) {
                Intent myIntent = new Intent(LaunchActivity.this, MenuActivity.class);
                startActivity(myIntent);
            } else {
                Intent myIntent = new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
            finish();
        },2000);
    }

}
