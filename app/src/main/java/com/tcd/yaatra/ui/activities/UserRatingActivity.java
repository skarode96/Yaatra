package com.tcd.yaatra.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityUserRatingBinding;
import com.tcd.yaatra.ui.adapter.UserRatingAdapter;

public class UserRatingActivity extends BaseActivity<ActivityUserRatingBinding> {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter urAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] user = {"Chetan", "Rohan", "Kavya"};

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_user_rating;
    }

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = (RecyclerView) layoutDataBinding.userList;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(UserRatingActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        urAdapter = new UserRatingAdapter(getApplicationContext(), user);
        recyclerView.setAdapter(urAdapter);
    }
}
