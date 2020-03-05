package com.tcd.yaatra.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityUserRatingBinding;
import com.tcd.yaatra.ui.adapter.UserRatingAdapter;

public class UserRatingActivity extends BaseActivity<ActivityUserRatingBinding> {

    private RecyclerView.Adapter urAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] user = {"Chetan", "Rohan", "Kavya"};

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_user_rating;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.userList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutDataBinding.userList.setLayoutManager(layoutManager);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urAdapter = new UserRatingAdapter(getApplicationContext(), user);
        layoutDataBinding.userList.setAdapter(urAdapter);
    }
}
