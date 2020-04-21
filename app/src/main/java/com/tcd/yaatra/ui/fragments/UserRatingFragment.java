package com.tcd.yaatra.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentUserRatingBinding;
import com.tcd.yaatra.repository.datasource.RatingRepository;
import com.tcd.yaatra.repository.models.TravellerInfo;
import com.tcd.yaatra.services.api.yaatra.models.RateRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.Rating;
import com.tcd.yaatra.ui.adapter.UserRatingAdapter;
import com.tcd.yaatra.ui.viewmodels.UserRatingFragmentViewModel;
import com.tcd.yaatra.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

public class UserRatingFragment extends BaseFragment<FragmentUserRatingBinding> {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter urAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TravellerInfo> peerTravellers;

    @Inject
    UserRatingFragmentViewModel userRatingActivityViewModel;

    @Inject
    RatingRepository ratingRepository;

    SharedPreferences ratingPreferences;

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_user_rating;
    }

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.Rate.setOnClickListener(view -> handleOnRateClick());
    }

    private void handleOnRateClick() {

        if(!NetworkUtils.isInternetAvailable(getActivity())){
            Runnable runnableTask = () -> {

                for (TravellerInfo travellerInfo : peerTravellers) {
                    Rating rating = new Rating();
                    rating.setUsername(travellerInfo.getUserName());
                    rating.setValue(travellerInfo.getUserRating());

                    ratingRepository.insertRating(rating);
                }
            };

            AsyncTask.execute(runnableTask);

            this.getActivity().recreate();
            return;
        }

        RateRequestBody rateRequestBody = new RateRequestBody();
        AtomicInteger count = new AtomicInteger();
        for (TravellerInfo travellerInfo: peerTravellers) {
            rateRequestBody.setUserName(travellerInfo.getUserName());
            rateRequestBody.setRating(travellerInfo.getUserRating());
//        rateRequestBody.setUserName("Jay");
//        rateRequestBody.setRating(2);

            userRatingActivityViewModel.rate(rateRequestBody).observe(this, rateResponse -> {
                switch (rateResponse.getState()) {
                    case LOADING:
                        Toast.makeText(this.getActivity(), "Updating the rating of the users", Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        count.getAndIncrement();
                        if (count.get() == peerTravellers.size())
                        Toast.makeText(this.getActivity(),"Rating updated", Toast.LENGTH_LONG).show();
                        break;
                    case FAILURE:
                        Log.e("TAG", "onFailure: "+rateResponse.getState().toString());
                        if (rateResponse.getData().getMessage().equals("Username not found")) {
                            Toast.makeText(this.getActivity(),"Username does not exist!", Toast.LENGTH_LONG).show();
                            break;
                        }
                        else {
                            Toast.makeText(this.getActivity(),"DB Error, try again!", Toast.LENGTH_LONG).show();
                            break;
                        }
                }
            });
        }
        if (count.get() == peerTravellers.size()) {

            this.getActivity().recreate();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        //this.ratingPreferences = SharedPreferenceUtils.getRatingSharedPreference();
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        peerTravellers = new ArrayList<>(Arrays.asList(gson.fromJson(bundle.getString("UserList"), TravellerInfo[].class)));
        recyclerView = (RecyclerView) layoutDataBinding.userList;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        urAdapter = new UserRatingAdapter(this.getActivity().getApplicationContext(), peerTravellers);
        recyclerView.setAdapter(urAdapter);

        return view;
    }
}
