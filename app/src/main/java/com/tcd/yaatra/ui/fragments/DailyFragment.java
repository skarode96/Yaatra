package com.tcd.yaatra.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentDailyBinding;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;
import com.tcd.yaatra.services.api.yaatra.models.JourneyDetails;
import com.tcd.yaatra.databinding.FragmentDailyCommuteMapBinding;
import com.tcd.yaatra.ui.activities.DailyCommuteMapFragment;
import com.tcd.yaatra.ui.adapters.DailyTripAdapter;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import java.util.ArrayList;

import javax.inject.Inject;

public class DailyFragment extends BaseFragment<FragmentDailyBinding> {

    SharedPreferences loginPreferences;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<JourneyDetails> data;
    public static View.OnClickListener dailyTripOnClickListener;


    @Inject
    DailyCommuteActivityViewModel dailyCommuteActivityViewModel;

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_daily;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        dailyTripOnClickListener = new DailyTripOnClickListener(this.getContext());

        this.loginPreferences = SharedPreferenceUtils.createLoginSharedPreference();
        data = new ArrayList<JourneyDetails>();

        final Context context = this.getActivity();
        FloatingActionButton fab = layoutDataBinding.fabNewTrip;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,new DailyCommuteMapFragment()).commit();
            }
        });

        recyclerView = (RecyclerView) layoutDataBinding.rvDailyTrips;
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dailyCommuteActivityViewModel.getDailyCommute().observe(getActivity(), dailyCommuteResponse -> {
            switch (dailyCommuteResponse.getState()){
                case LOADING:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "daily commute creation successful", Toast.LENGTH_SHORT).show();
//                        Intent myIntent = new Intent(getActivity(), DailyCommuteListActivity.class);
//                        startActivity(myIntent);

                    for (int i = 0; i < dailyCommuteResponse.getData().getJourneyDetails().size(); i++) {

                        JourneyDetails j = dailyCommuteResponse.getData().getJourneyDetails().get(i);
                        data.add(j);
                    }
                    adapter = new DailyTripAdapter(data);
                    recyclerView.setAdapter(adapter);
                    break;

                case FAILURE:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), dailyCommuteResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });

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

