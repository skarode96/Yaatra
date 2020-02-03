package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityDailyCommuteBinding;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;

import javax.inject.Inject;

public class DailyCommuteActivity extends BaseActivity<ActivityDailyCommuteBinding> {

    private SharedPreferences preferences;
    private String savedToken;

    @Inject
    DailyCommuteActivityViewModel dailyCommuteActivityViewModel;

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_daily_commute;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.btnDailyCommute.setOnClickListener(view -> handleDailyCommuteClick());
        layoutDataBinding.btnShowRoute.setOnClickListener(view -> handleShowRoute());
        layoutDataBinding.btnFindCoTraveller.setOnClickListener(view -> handleFindCoTravellers());
        
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.preferences = getApplicationContext().getSharedPreferences("LoginPref", 0); // 0 - for private mode
        this.savedToken = this.preferences.getString("token", "no token");
    }

    private void handleFindCoTravellers() {
        Intent myIntent = new Intent(DailyCommuteActivity.this, PeerToPeerActivity.class);
        startActivity(myIntent);
    }

    private void handleShowRoute() {
        Intent myIntent = new Intent(DailyCommuteActivity.this, MapBoxActivity.class);
        startActivity(myIntent);
    }

    private void handleDailyCommuteClick() {
        dailyCommuteActivityViewModel.getDailyCommute(this.savedToken).observe(this, dailyCommuteResponse -> {
            switch (dailyCommuteResponse.getState()) {
                case LOADING:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                    Intent myIntent = new Intent(DailyCommuteActivity.this, DailyCommuteListActivity.class);
                    myIntent.putExtra("user", dailyCommuteResponse.toString());
                    startActivity(myIntent);
                    break;

                case FAILURE:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

}
