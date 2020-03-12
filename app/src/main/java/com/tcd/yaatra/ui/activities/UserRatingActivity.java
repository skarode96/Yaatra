package com.tcd.yaatra.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityUserRatingBinding;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.ui.adapter.UserRatingAdapter;

import java.util.ArrayList;

public class UserRatingActivity extends BaseActivity<ActivityUserRatingBinding> {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter urAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TravellerInfo> user = new ArrayList<>();

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
        Toast.makeText(this, "For User 1 "+user.get(0).getUserRating(), Toast.LENGTH_LONG).show();
        /* Write Code to upload to database in the backend*/
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        user = (ArrayList<TravellerInfo>)bundle.getSerializable("UserList");
        recyclerView = (RecyclerView) layoutDataBinding.userList;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(UserRatingActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        urAdapter = new UserRatingAdapter(getApplicationContext(), user);
        recyclerView.setAdapter(urAdapter);
    }

}
