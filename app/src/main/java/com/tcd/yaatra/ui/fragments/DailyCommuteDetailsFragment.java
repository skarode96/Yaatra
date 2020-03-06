package com.tcd.yaatra.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentDailyCommuteDetailsBinding;
import com.tcd.yaatra.services.api.yaatra.models.JourneyDetails;
import com.tcd.yaatra.services.api.yaatra.models.TravellerDetails;
import com.tcd.yaatra.ui.adapters.DailyCommuteDetailsAdapter;
import com.tcd.yaatra.ui.adapters.DailyTripAdapter;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.DailyCommuteDetailsViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

public class DailyCommuteDetailsFragment extends BaseFragment<FragmentDailyCommuteDetailsBinding> {

    private static RecyclerView.Adapter adapter;
    private static RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<TravellerDetails> data;

    @Inject
    DailyCommuteDetailsViewModel dailyCommuteDetailsViewModel;

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_daily_commute_details;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        int journeyId = getArguments().getInt("journeyId");

        data = new ArrayList<TravellerDetails>();
        final Context context = this.getActivity();

        recyclerView = (RecyclerView) layoutDataBinding.rvDailyCommuteDetails;
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dailyCommuteDetailsViewModel.getDailyCommuteDetails(journeyId).observe(getActivity(),dailyCommuteDetailsResponse -> {
            switch (dailyCommuteDetailsResponse.getState()){
                case LOADING:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "daily commute creation successful", Toast.LENGTH_SHORT).show();
//                        Intent myIntent = new Intent(getActivity(), DailyCommuteListActivity.class);
//                        startActivity(myIntent);

                    for (int i = 0; i < dailyCommuteDetailsResponse.getData().getTravellerDetails().size(); i++) {

                        TravellerDetails tD = dailyCommuteDetailsResponse.getData().getTravellerDetails().get(i);
                        data.add(tD);
                    }
                    adapter = new DailyCommuteDetailsAdapter(data);
                    recyclerView.setAdapter(adapter);
                    break;

                case FAILURE:
                    layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), dailyCommuteDetailsResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        return view;
    }
}
