package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityUserRatingBinding;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.services.api.yaatra.models.RateRequestBody;
import com.tcd.yaatra.ui.adapter.UserRatingAdapter;
import com.tcd.yaatra.ui.viewmodels.UserRatingActivityViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

public class UserRatingActivity extends BaseActivity<ActivityUserRatingBinding> {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter urAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TravellerInfo> user;

    @Inject
    UserRatingActivityViewModel userRatingActivityViewModel;
    SharedPreferences ratingPreferences;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_user_rating;
    }

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.Rate.setOnClickListener(view -> handleOnRateClick());
    }

    private void handleOnRateClick() {
        RateRequestBody rateRequestBody = new RateRequestBody();
        AtomicInteger count = new AtomicInteger();
        for (TravellerInfo travellerInfo: user) {
            rateRequestBody.setUserName(travellerInfo.getUserName());
            rateRequestBody.setRating(travellerInfo.getUserRating());
//        rateRequestBody.setUserName("Jay");
//        rateRequestBody.setRating(2);

            userRatingActivityViewModel.rate(rateRequestBody).observe(this, rateResponse -> {
                switch (rateResponse.getState()) {
                    case LOADING:
                        Toast.makeText(this, "Updating the rating of the users", Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        count.getAndIncrement();
                        if (count.get() == user.size())
                        Toast.makeText(this,"Rating updated", Toast.LENGTH_LONG).show();
                        break;
                    case FAILURE:
                        Log.e("TAG", "onFailure: "+rateResponse.getState().toString());
                        if (rateResponse.getData().getMessage().equals("Username not found")) {
                            Toast.makeText(this,"Username does not exist!", Toast.LENGTH_LONG).show();
                            break;
                        }
                        else {
                            Toast.makeText(this,"DB Error, try again!", Toast.LENGTH_LONG).show();
                            break;
                        }
                }
            });
        }
        if (count.get() == user.size()) {
            Intent backToHome = new Intent(this,LaunchActivity.class);
            startActivity(backToHome);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.ratingPreferences = SharedPreferenceUtils.getRatingSharedPreference();
        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        user = new ArrayList<>(Arrays.asList(gson.fromJson(bundle.getString("UserList"), TravellerInfo[].class)));
        recyclerView = (RecyclerView) layoutDataBinding.userList;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(UserRatingActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        urAdapter = new UserRatingAdapter(getApplicationContext(), user);
        recyclerView.setAdapter(urAdapter);
    }

}
