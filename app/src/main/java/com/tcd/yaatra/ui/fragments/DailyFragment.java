package com.tcd.yaatra.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentDailyBinding;
import com.tcd.yaatra.services.api.yaatra.models.JourneyDetails;
import com.tcd.yaatra.ui.activities.LoginActivity;
import com.tcd.yaatra.ui.activities.MenuContainerActivity;
import com.tcd.yaatra.ui.adapters.DailyTripAdapter;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import java.util.ArrayList;
import javax.inject.Inject;

public class DailyFragment extends BaseFragment<FragmentDailyBinding> {

    private static RecyclerView.Adapter adapter;
    private static RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<JourneyDetails> data;
    public static View.OnClickListener dailyTripOnClickListener;


    @Inject
    protected DailyCommuteActivityViewModel dailyCommuteActivityViewModel;

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_daily;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        dailyTripOnClickListener = new DailyTripOnClickListener(this.getContext());

        data = new ArrayList<JourneyDetails>();

        final Context context = this.getActivity();
        FloatingActionButton fab = layoutDataBinding.fabNewTrip;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Toast.makeText(getActivity(), "yaatra service is down. Please come after some time", Toast.LENGTH_SHORT).show();
                    handleLogout();
                    break;
            }
        });

        if(getActivity() instanceof MenuContainerActivity) {
            ((MenuContainerActivity) getActivity()).layoutDataBinding.toolbar.setTitle("Yaatra Daily Commute");
        }
        return view;
    }
    private void handleLogout() {
        SharedPreferenceUtils.clearAuthToken();
        SharedPreferenceUtils.clearUserName();
        SharedPreferenceUtils.clearUserId();
        Intent myIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(myIntent);
        //finish();
    }

    private class DailyTripOnClickListener implements View.OnClickListener {

        private final Context context;

        private DailyTripOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int itemPosition = recyclerView.getChildLayoutPosition(v);
            showDetails(v,itemPosition);
        }

        private void showDetails(View v, int item) {
            Intent dailyDetailsIntent = new Intent(context, DailyCommuteDetailsFragment.class);
            Bundle bundle = new Bundle();
            bundle.putInt("journeyId",data.get(item).getJourneyId());
            dailyDetailsIntent.putExtras(bundle);
            Fragment f = new DailyCommuteDetailsFragment();
            f.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,f).commit();
        }
    }
}

