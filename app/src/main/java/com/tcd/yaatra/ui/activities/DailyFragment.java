package com.tcd.yaatra.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentDailyBinding;
import com.tcd.yaatra.services.api.yaatra.models.UserInfo;
import com.tcd.yaatra.ui.fragments.BaseFragment;
import com.tcd.yaatra.utils.DatabaseUtils;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

public class DailyFragment extends BaseFragment<FragmentDailyBinding> {

    SharedPreferences loginPreferences;

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_daily;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.loginPreferences = SharedPreferenceUtils.createLoginSharedPreference();
        //TextView textView = (TextView) (this.layoutDataBinding.text1);

        DatabaseUtils userDb = DatabaseUtils.getInstance(getActivity());
        UserInfo user = userDb.userInfoDao().getUserProfile(SharedPreferenceUtils.getUserName());
        //textView.setText(user.getUsername());
        return view;
    }


}

