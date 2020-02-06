package com.tcd.yaatra.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityDailyCommuteBinding;
import com.tcd.yaatra.databinding.ActivityNavigationDrawerBinding;

public class NavigationDrawerActivity extends BaseActivity<ActivityNavigationDrawerBinding> {

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_navigation_drawer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
