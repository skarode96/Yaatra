package com.tcd.yaatra.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentDailyBinding;
import com.tcd.yaatra.databinding.FragmentDailyCommuteMapBinding;
import com.tcd.yaatra.services.api.yaatra.models.Journey;
import com.tcd.yaatra.ui.adapters.DailyTripAdapter;
import com.tcd.yaatra.ui.fragments.BaseFragment;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import java.util.ArrayList;

public class DailyFragment extends BaseFragment<FragmentDailyBinding> {

    SharedPreferences loginPreferences;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Journey> data;
    public static View.OnClickListener dailyTripOnClickListener;

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_daily;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        dailyTripOnClickListener = new DailyTripOnClickListener(this.getContext());

        this.loginPreferences = SharedPreferenceUtils.createLoginSharedPreference();

        final Context context = this.getActivity();
        FloatingActionButton fab = layoutDataBinding.fabNewTrip;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = (RecyclerView) layoutDataBinding.rvDailyTrips;
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<Journey>();
        for (int i = 0; i < 3; i++) {

            Journey j = new Journey();
            j.setJourneyId(String.valueOf(i));
            j.setTitle("Title"+i);

            data.add(j);
        }

        adapter = new DailyTripAdapter(data);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private static class DailyTripOnClickListener implements View.OnClickListener {

        private final Context context;

        private DailyTripOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            showDetails(v);
        }

        private void showDetails(View v) {
            Toast.makeText(context, "List Item Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}

