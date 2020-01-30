package com.tcd.yaatra.ui.activities;

import android.os.Bundle;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityDailyCommuteListBinding;

public class DailyCommuteListActivity extends BaseActivity<ActivityDailyCommuteListBinding> {

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_daily_commute_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String user = extras.getString("user");
            layoutDataBinding.userView.setText(user);
        }
    }
}
